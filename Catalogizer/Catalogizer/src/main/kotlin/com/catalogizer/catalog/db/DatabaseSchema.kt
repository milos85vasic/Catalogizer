package com.catalogizer.catalog.db

object DatabaseSchema {

    // Schema version for migrations
    const val SCHEMA_VERSION = 1

    // Table definitions
    const val CREATE_SMB_ROOTS_TABLE = """
        CREATE TABLE IF NOT EXISTS smb_roots (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE,
            host TEXT NOT NULL,
            port INTEGER NOT NULL DEFAULT 445,
            share TEXT NOT NULL,
            username TEXT NOT NULL,
            password TEXT NOT NULL,
            domain TEXT DEFAULT '',
            enabled INTEGER NOT NULL DEFAULT 1,
            scan_interval_minutes INTEGER NOT NULL DEFAULT 60,
            created_at INTEGER NOT NULL,
            updated_at INTEGER NOT NULL,
            last_scan_at INTEGER DEFAULT NULL
        )
    """

    const val CREATE_FILES_TABLE = """
        CREATE TABLE IF NOT EXISTS files (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            smb_root_id INTEGER NOT NULL,
            path TEXT NOT NULL,
            name TEXT NOT NULL,
            parent_path TEXT,
            size_bytes INTEGER NOT NULL DEFAULT 0,
            is_directory INTEGER NOT NULL DEFAULT 0,
            created_at INTEGER NOT NULL,
            modified_at INTEGER NOT NULL,
            accessed_at INTEGER,
            discovered_at INTEGER NOT NULL,
            last_verified_at INTEGER NOT NULL,

            -- Hashing for duplicate detection
            md5_hash TEXT,
            sha256_hash TEXT,
            blake3_hash TEXT,
            content_hash TEXT, -- Primary hash for duplicate detection

            -- File type and extension
            extension TEXT,
            mime_type TEXT,
            file_type_category TEXT, -- image, video, audio, document, etc.

            -- Status flags
            is_duplicate INTEGER NOT NULL DEFAULT 0,
            is_accessible INTEGER NOT NULL DEFAULT 1,
            is_deleted INTEGER NOT NULL DEFAULT 0,

            FOREIGN KEY (smb_root_id) REFERENCES smb_roots(id) ON DELETE CASCADE
        )
    """

    const val CREATE_FILE_METADATA_TABLE = """
        CREATE TABLE IF NOT EXISTS file_metadata (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            file_id INTEGER NOT NULL,
            metadata_key TEXT NOT NULL,
            metadata_value TEXT,
            metadata_type TEXT NOT NULL, -- string, number, date, boolean

            -- For searchable text content
            searchable_content TEXT,

            created_at INTEGER NOT NULL,

            FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
            UNIQUE(file_id, metadata_key)
        )
    """

    const val CREATE_DUPLICATES_TABLE = """
        CREATE TABLE IF NOT EXISTS duplicates (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            hash_value TEXT NOT NULL,
            hash_type TEXT NOT NULL, -- md5, sha256, blake3, content
            file_count INTEGER NOT NULL DEFAULT 0,
            total_size_bytes INTEGER NOT NULL DEFAULT 0,
            first_discovered_at INTEGER NOT NULL,
            last_updated_at INTEGER NOT NULL
        )
    """

    const val CREATE_DUPLICATE_FILES_TABLE = """
        CREATE TABLE IF NOT EXISTS duplicate_files (
            duplicate_id INTEGER NOT NULL,
            file_id INTEGER NOT NULL,
            added_at INTEGER NOT NULL,

            PRIMARY KEY (duplicate_id, file_id),
            FOREIGN KEY (duplicate_id) REFERENCES duplicates(id) ON DELETE CASCADE,
            FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE
        )
    """

    const val CREATE_VIRTUAL_TREE_TABLE = """
        CREATE TABLE IF NOT EXISTS virtual_tree (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            virtual_path TEXT NOT NULL UNIQUE,
            target_file_id INTEGER,
            is_directory INTEGER NOT NULL DEFAULT 0,
            parent_virtual_path TEXT,
            created_at INTEGER NOT NULL,
            updated_at INTEGER NOT NULL,

            FOREIGN KEY (target_file_id) REFERENCES files(id) ON DELETE SET NULL
        )
    """

    const val CREATE_SCAN_HISTORY_TABLE = """
        CREATE TABLE IF NOT EXISTS scan_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            smb_root_id INTEGER NOT NULL,
            scan_type TEXT NOT NULL, -- full, incremental, verification
            started_at INTEGER NOT NULL,
            completed_at INTEGER,
            files_discovered INTEGER DEFAULT 0,
            files_updated INTEGER DEFAULT 0,
            files_deleted INTEGER DEFAULT 0,
            errors_count INTEGER DEFAULT 0,
            status TEXT NOT NULL, -- running, completed, failed, cancelled
            error_message TEXT,

            FOREIGN KEY (smb_root_id) REFERENCES smb_roots(id) ON DELETE CASCADE
        )
    """

    // Indexes for performance
    val INDEXES = listOf(
        // Files table indexes
        "CREATE INDEX IF NOT EXISTS idx_files_smb_root ON files(smb_root_id)",
        "CREATE INDEX IF NOT EXISTS idx_files_path ON files(path)",
        "CREATE INDEX IF NOT EXISTS idx_files_name ON files(name)",
        "CREATE INDEX IF NOT EXISTS idx_files_parent_path ON files(parent_path)",
        "CREATE INDEX IF NOT EXISTS idx_files_size ON files(size_bytes)",
        "CREATE INDEX IF NOT EXISTS idx_files_modified ON files(modified_at)",
        "CREATE INDEX IF NOT EXISTS idx_files_extension ON files(extension)",
        "CREATE INDEX IF NOT EXISTS idx_files_mime_type ON files(mime_type)",
        "CREATE INDEX IF NOT EXISTS idx_files_category ON files(file_type_category)",
        "CREATE INDEX IF NOT EXISTS idx_files_is_directory ON files(is_directory)",
        "CREATE INDEX IF NOT EXISTS idx_files_is_duplicate ON files(is_duplicate)",
        "CREATE INDEX IF NOT EXISTS idx_files_is_accessible ON files(is_accessible)",
        "CREATE INDEX IF NOT EXISTS idx_files_is_deleted ON files(is_deleted)",

        // Hash indexes for duplicate detection
        "CREATE INDEX IF NOT EXISTS idx_files_md5_hash ON files(md5_hash)",
        "CREATE INDEX IF NOT EXISTS idx_files_sha256_hash ON files(sha256_hash)",
        "CREATE INDEX IF NOT EXISTS idx_files_blake3_hash ON files(blake3_hash)",
        "CREATE INDEX IF NOT EXISTS idx_files_content_hash ON files(content_hash)",

        // Composite indexes for common queries
        "CREATE INDEX IF NOT EXISTS idx_files_root_path ON files(smb_root_id, path)",
        "CREATE INDEX IF NOT EXISTS idx_files_root_accessible ON files(smb_root_id, is_accessible, is_deleted)",
        "CREATE INDEX IF NOT EXISTS idx_files_type_size ON files(file_type_category, size_bytes)",
        "CREATE INDEX IF NOT EXISTS idx_files_modified_size ON files(modified_at, size_bytes)",

        // Metadata indexes
        "CREATE INDEX IF NOT EXISTS idx_metadata_file ON file_metadata(file_id)",
        "CREATE INDEX IF NOT EXISTS idx_metadata_key ON file_metadata(metadata_key)",
        "CREATE INDEX IF NOT EXISTS idx_metadata_value ON file_metadata(metadata_value)",
        "CREATE INDEX IF NOT EXISTS idx_metadata_type ON file_metadata(metadata_type)",
        "CREATE INDEX IF NOT EXISTS idx_metadata_key_value ON file_metadata(metadata_key, metadata_value)",

        // Full-text search index
        "CREATE INDEX IF NOT EXISTS idx_metadata_searchable ON file_metadata(searchable_content)",

        // Duplicates indexes
        "CREATE INDEX IF NOT EXISTS idx_duplicates_hash ON duplicates(hash_value, hash_type)",
        "CREATE INDEX IF NOT EXISTS idx_duplicates_count ON duplicates(file_count)",
        "CREATE INDEX IF NOT EXISTS idx_duplicates_size ON duplicates(total_size_bytes)",

        // Virtual tree indexes
        "CREATE INDEX IF NOT EXISTS idx_virtual_tree_path ON virtual_tree(virtual_path)",
        "CREATE INDEX IF NOT EXISTS idx_virtual_tree_parent ON virtual_tree(parent_virtual_path)",
        "CREATE INDEX IF NOT EXISTS idx_virtual_tree_target ON virtual_tree(target_file_id)",
        "CREATE INDEX IF NOT EXISTS idx_virtual_tree_directory ON virtual_tree(is_directory)",

        // Scan history indexes
        "CREATE INDEX IF NOT EXISTS idx_scan_history_root ON scan_history(smb_root_id)",
        "CREATE INDEX IF NOT EXISTS idx_scan_history_started ON scan_history(started_at)",
        "CREATE INDEX IF NOT EXISTS idx_scan_history_status ON scan_history(status)",

        // SMB roots indexes
        "CREATE INDEX IF NOT EXISTS idx_smb_roots_enabled ON smb_roots(enabled)",
        "CREATE INDEX IF NOT EXISTS idx_smb_roots_last_scan ON smb_roots(last_scan_at)"
    )

    // Views for common queries
    const val CREATE_DUPLICATE_FILES_VIEW = """
        CREATE VIEW IF NOT EXISTS v_duplicate_files AS
        SELECT
            f.*,
            d.hash_value,
            d.hash_type,
            d.file_count,
            d.total_size_bytes,
            sr.name as smb_root_name,
            sr.host,
            sr.share
        FROM files f
        JOIN duplicate_files df ON f.id = df.file_id
        JOIN duplicates d ON df.duplicate_id = d.id
        JOIN smb_roots sr ON f.smb_root_id = sr.id
        WHERE f.is_duplicate = 1 AND f.is_deleted = 0
    """

    const val CREATE_VIRTUAL_FILES_VIEW = """
        CREATE VIEW IF NOT EXISTS v_virtual_files AS
        SELECT
            vt.*,
            f.name as file_name,
            f.size_bytes,
            f.modified_at,
            f.extension,
            f.mime_type,
            f.file_type_category,
            sr.name as smb_root_name
        FROM virtual_tree vt
        LEFT JOIN files f ON vt.target_file_id = f.id
        LEFT JOIN smb_roots sr ON f.smb_root_id = sr.id
    """

    const val CREATE_FILE_STATS_VIEW = """
        CREATE VIEW IF NOT EXISTS v_file_stats AS
        SELECT
            sr.id as smb_root_id,
            sr.name as smb_root_name,
            COUNT(f.id) as total_files,
            SUM(CASE WHEN f.is_directory = 0 THEN 1 ELSE 0 END) as file_count,
            SUM(CASE WHEN f.is_directory = 1 THEN 1 ELSE 0 END) as directory_count,
            SUM(f.size_bytes) as total_size_bytes,
            SUM(CASE WHEN f.is_duplicate = 1 THEN 1 ELSE 0 END) as duplicate_count,
            MAX(f.last_verified_at) as last_verified_at
        FROM smb_roots sr
        LEFT JOIN files f ON sr.id = f.smb_root_id
            AND f.is_deleted = 0 AND f.is_accessible = 1
        GROUP BY sr.id, sr.name
    """

    // Triggers for maintaining data consistency
    const val CREATE_UPDATE_TIMESTAMPS_TRIGGER = """
        CREATE TRIGGER IF NOT EXISTS update_file_timestamp
        AFTER UPDATE ON files
        FOR EACH ROW
        BEGIN
            UPDATE files SET last_verified_at = strftime('%s', 'now') WHERE id = NEW.id;
        END
    """

    const val CREATE_DUPLICATE_COUNT_TRIGGER = """
        CREATE TRIGGER IF NOT EXISTS update_duplicate_count
        AFTER INSERT ON duplicate_files
        FOR EACH ROW
        BEGIN
            UPDATE duplicates
            SET file_count = (
                SELECT COUNT(*) FROM duplicate_files WHERE duplicate_id = NEW.duplicate_id
            ),
            last_updated_at = strftime('%s', 'now')
            WHERE id = NEW.duplicate_id;
        END
    """
}