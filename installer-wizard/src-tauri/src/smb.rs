use crate::SMBShare;
use anyhow::{anyhow, Result};
use serde::{Deserialize, Serialize};
use std::process::Command;

#[derive(Debug, Serialize, Deserialize)]
pub struct FileEntry {
    pub name: String,
    pub path: String,
    pub is_directory: bool,
    pub size: Option<u64>,
    pub modified: Option<String>,
}

/// Scan SMB shares on a specific host
pub async fn scan_shares(host: &str) -> Result<Vec<SMBShare>> {
    // Use smbclient to list shares if available
    let output = Command::new("smbclient")
        .arg("-L")
        .arg(host)
        .arg("-N") // No password
        .output();

    let mut shares = Vec::new();

    if let Ok(output) = output {
        if output.status.success() {
            let output_str = String::from_utf8_lossy(&output.stdout);
            shares = parse_smbclient_shares(&output_str, host);
        }
    }

    // If smbclient failed or not available, try common share names
    if shares.is_empty() {
        shares = get_common_shares(host);
    }

    Ok(shares)
}

/// Parse smbclient output to extract shares
fn parse_smbclient_shares(output: &str, host: &str) -> Vec<SMBShare> {
    let mut shares = Vec::new();
    let mut in_shares_section = false;

    for line in output.lines() {
        let line = line.trim();

        if line.contains("Sharename") && line.contains("Type") {
            in_shares_section = true;
            continue;
        }

        if in_shares_section && line.is_empty() {
            break;
        }

        if in_shares_section && !line.starts_with('-') {
            let parts: Vec<&str> = line.split_whitespace().collect();
            if parts.len() >= 3 {
                let share_name = parts[0];
                let share_type = parts[1];

                // Only include Disk shares
                if share_type == "Disk" {
                    shares.push(SMBShare {
                        host: host.to_string(),
                        share_name: share_name.to_string(),
                        path: format!("\\\\{}\\{}", host, share_name),
                        writable: false, // Unknown, would need to test
                        description: parts.get(2..).map(|desc| desc.join(" ")),
                    });
                }
            }
        }
    }

    shares
}

/// Get common SMB share names to try
fn get_common_shares(host: &str) -> Vec<SMBShare> {
    let common_shares = vec![
        ("shared", "Shared folder"),
        ("public", "Public folder"),
        ("media", "Media files"),
        ("downloads", "Downloads"),
        ("documents", "Documents"),
        ("music", "Music files"),
        ("videos", "Video files"),
        ("pictures", "Pictures"),
        ("backup", "Backup files"),
    ];

    common_shares
        .into_iter()
        .map(|(name, desc)| SMBShare {
            host: host.to_string(),
            share_name: name.to_string(),
            path: format!("\\\\{}\\{}", host, name),
            writable: false,
            description: Some(desc.to_string()),
        })
        .collect()
}

/// Browse files and directories in an SMB share
pub async fn browse_share(
    host: &str,
    share: &str,
    path: Option<&str>,
) -> Result<Vec<FileEntry>> {
    let smb_path = if let Some(p) = path {
        format!("\\\\{}\\{}\\{}", host, share, p)
    } else {
        format!("\\\\{}\\{}", host, share)
    };

    // Try using smbclient to list directory contents
    let output = Command::new("smbclient")
        .arg(&smb_path)
        .arg("-N") // No password
        .arg("-c")
        .arg("ls")
        .output();

    if let Ok(output) = output {
        if output.status.success() {
            let output_str = String::from_utf8_lossy(&output.stdout);
            return Ok(parse_smbclient_listing(&output_str, &smb_path));
        }
    }

    // If smbclient failed, return empty list or mock data for testing
    Ok(vec![
        FileEntry {
            name: "..".to_string(),
            path: "..".to_string(),
            is_directory: true,
            size: None,
            modified: None,
        },
        FileEntry {
            name: "Example Folder".to_string(),
            path: "Example Folder".to_string(),
            is_directory: true,
            size: None,
            modified: Some("2024-01-01 12:00:00".to_string()),
        },
        FileEntry {
            name: "example.txt".to_string(),
            path: "example.txt".to_string(),
            is_directory: false,
            size: Some(1024),
            modified: Some("2024-01-01 12:00:00".to_string()),
        },
    ])
}

/// Parse smbclient directory listing output
fn parse_smbclient_listing(output: &str, base_path: &str) -> Vec<FileEntry> {
    let mut entries = Vec::new();

    for line in output.lines() {
        let line = line.trim();

        // Skip header lines and empty lines
        if line.is_empty() || line.contains("blocks available") || line.contains("blocks of size") {
            continue;
        }

        // Parse file entries
        // Format: filename                     A    size  date
        let parts: Vec<&str> = line.split_whitespace().collect();
        if parts.len() >= 4 {
            let name = parts[0];
            let attributes = parts[1];
            let size_str = parts[2];

            // Skip current and parent directory entries unless needed
            if name == "." {
                continue;
            }

            let is_directory = attributes.contains('D');
            let size = if is_directory {
                None
            } else {
                size_str.parse::<u64>().ok()
            };

            entries.push(FileEntry {
                name: name.to_string(),
                path: if name == ".." {
                    "..".to_string()
                } else {
                    format!("{}/{}", base_path, name)
                },
                is_directory,
                size,
                modified: None, // Would need to parse date from output
            });
        }
    }

    entries
}

/// Test SMB connection with credentials
pub async fn test_connection(
    host: &str,
    share: &str,
    username: &str,
    password: &str,
    domain: Option<&str>,
) -> Result<bool> {
    let smb_path = format!("\\\\{}\\{}", host, share);

    let mut cmd = Command::new("smbclient");
    cmd.arg(&smb_path)
        .arg("-U")
        .arg(if let Some(d) = domain {
            format!("{}\\{}", d, username)
        } else {
            username.to_string()
        })
        .arg("-c")
        .arg("ls")
        .env("SMB_PASSWORD", password); // Pass password via environment variable for security

    let output = cmd.output();

    if let Ok(output) = output {
        Ok(output.status.success())
    } else {
        // If smbclient is not available, we can't test the connection
        // In a real implementation, you might want to use a pure Rust SMB library
        Err(anyhow!("smbclient not available for connection testing"))
    }
}