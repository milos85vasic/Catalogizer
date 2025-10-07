use crate::NetworkHost;
use anyhow::Result;
use network_interface::{NetworkInterface, NetworkInterfaceConfig};
use std::net::{IpAddr, Ipv4Addr};
use std::process::Command;
use std::time::Duration;
use tokio::net::TcpStream;
use tokio::time::timeout;

/// Scan the local network for hosts
pub async fn scan_network() -> Result<Vec<NetworkHost>> {
    let interfaces = NetworkInterface::show()?;
    let mut hosts = Vec::new();

    for interface in interfaces {
        if !interface.addr.is_empty() {
            if let Some(network) = get_network_range(&interface) {
                let network_hosts = scan_network_range(network).await?;
                hosts.extend(network_hosts);
            }
        }
    }

    // Remove duplicates based on IP address
    hosts.sort_by(|a, b| a.ip.cmp(&b.ip));
    hosts.dedup_by(|a, b| a.ip == b.ip);

    Ok(hosts)
}

/// Get network range from interface
fn get_network_range(interface: &NetworkInterface) -> Option<ipnetwork::Ipv4Network> {
    for addr in &interface.addr {
        if let IpAddr::V4(ipv4) = addr.ip() {
            // Assume /24 network for simplicity
            if let Ok(network) = ipnetwork::Ipv4Network::new(ipv4, 24) {
                return Some(network);
            }
        }
    }
    None
}

/// Scan a network range for active hosts
async fn scan_network_range(network: ipnetwork::Ipv4Network) -> Result<Vec<NetworkHost>> {
    let mut hosts = Vec::new();
    let mut tasks = Vec::new();

    // Create async tasks for each IP in the network
    for ip in network.iter() {
        let task = tokio::spawn(async move {
            if is_host_alive(ip).await {
                Some(scan_host(ip).await)
            } else {
                None
            }
        });
        tasks.push(task);
    }

    // Wait for all tasks to complete
    for task in tasks {
        if let Ok(Some(Ok(host))) = task.await {
            hosts.push(host);
        }
    }

    Ok(hosts)
}

/// Check if a host is alive using ping
async fn is_host_alive(ip: Ipv4Addr) -> bool {
    // Try to connect to common ports first (faster than ping)
    let ports = vec![22, 80, 135, 139, 443, 445]; // Include SMB ports 135, 139, 445

    for port in ports {
        if timeout(
            Duration::from_millis(100),
            TcpStream::connect((ip, port))
        ).await.is_ok() {
            return true;
        }
    }

    // Fallback to system ping
    let output = Command::new("ping")
        .arg("-c")
        .arg("1")
        .arg("-W")
        .arg("1000") // 1 second timeout
        .arg(ip.to_string())
        .output();

    if let Ok(output) = output {
        output.status.success()
    } else {
        false
    }
}

/// Scan a specific host for information
async fn scan_host(ip: Ipv4Addr) -> Result<NetworkHost> {
    let hostname = resolve_hostname(ip).await;
    let mac_address = get_mac_address(ip).await;
    let vendor = None; // Could implement MAC vendor lookup
    let open_ports = scan_ports(ip).await?;
    let smb_shares = if open_ports.contains(&445) || open_ports.contains(&139) {
        scan_smb_shares_for_host(ip).await.unwrap_or_default()
    } else {
        Vec::new()
    };

    Ok(NetworkHost {
        ip: ip.to_string(),
        hostname,
        mac_address,
        vendor,
        open_ports,
        smb_shares,
    })
}

/// Resolve hostname for an IP address
async fn resolve_hostname(ip: Ipv4Addr) -> Option<String> {
    use trust_dns_resolver::TokioAsyncResolver;
    use trust_dns_resolver::config::*;

    // TokioAsyncResolver::tokio() returns the resolver directly, not a Result
    let resolver = TokioAsyncResolver::tokio(
        ResolverConfig::default(),
        ResolverOpts::default(),
    );

    if let Ok(response) = resolver.reverse_lookup(IpAddr::V4(ip)).await {
        return response.iter().next().map(|name| name.to_string());
    }

    None
}

/// Get MAC address for an IP (requires ARP table access)
async fn get_mac_address(ip: Ipv4Addr) -> Option<String> {
    // Try to get MAC from ARP table
    let output = Command::new("arp")
        .arg("-n")
        .arg(ip.to_string())
        .output()
        .ok()?;

    if output.status.success() {
        let output_str = String::from_utf8(output.stdout).ok()?;
        // Parse ARP output to extract MAC address
        // Format varies by OS, this is a simplified version
        for line in output_str.lines() {
            if line.contains(&ip.to_string()) {
                let parts: Vec<&str> = line.split_whitespace().collect();
                if parts.len() >= 3 {
                    let mac = parts[2];
                    if mac.contains(':') && mac.len() == 17 {
                        return Some(mac.to_string());
                    }
                }
            }
        }
    }

    None
}

/// Scan common ports on a host
async fn scan_ports(ip: Ipv4Addr) -> Result<Vec<u16>> {
    let common_ports = vec![
        21, 22, 23, 25, 53, 80, 110, 135, 139, 143, 443, 445, 993, 995, 3389, 5985, 5986
    ];
    let mut open_ports = Vec::new();
    let mut tasks = Vec::new();

    for port in common_ports {
        let task = tokio::spawn(async move {
            if timeout(
                Duration::from_millis(200),
                TcpStream::connect((ip, port))
            ).await.is_ok() {
                Some(port)
            } else {
                None
            }
        });
        tasks.push(task);
    }

    for task in tasks {
        if let Ok(Some(port)) = task.await {
            open_ports.push(port);
        }
    }

    Ok(open_ports)
}

/// Scan SMB shares for a specific host
async fn scan_smb_shares_for_host(_ip: Ipv4Addr) -> Result<Vec<String>> {
    // This is a simplified implementation
    // In a real implementation, you would use SMB protocol to enumerate shares
    let mut shares = Vec::new();

    // Try common share names
    let common_shares = vec!["C$", "ADMIN$", "IPC$", "shared", "public", "media", "downloads"];

    for share in common_shares {
        // This is a placeholder - in reality you'd need to implement SMB enumeration
        // For now, we'll just return common shares
        shares.push(share.to_string());
    }

    Ok(shares)
}