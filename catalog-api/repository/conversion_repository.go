package repository

import (
	"database/sql"
	"fmt"
	"time"

	"catalogizer/models"
)

type ConversionRepository struct {
	db *sql.DB
}

func NewConversionRepository(db *sql.DB) *ConversionRepository {
	return &ConversionRepository{db: db}
}

func (r *ConversionRepository) CreateJob(job *models.ConversionJob) (int, error) {
	query := `
		INSERT INTO conversion_jobs (user_id, source_path, target_path, source_format, target_format,
									conversion_type, quality, settings, priority, status, created_at, scheduled_for)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	`

	result, err := r.db.Exec(query,
		job.UserID, job.SourcePath, job.TargetPath, job.SourceFormat, job.TargetFormat,
		job.ConversionType, job.Quality, job.Settings, job.Priority, job.Status,
		job.CreatedAt, job.ScheduledFor)

	if err != nil {
		return 0, fmt.Errorf("failed to create conversion job: %w", err)
	}

	id, err := result.LastInsertId()
	if err != nil {
		return 0, fmt.Errorf("failed to get job ID: %w", err)
	}

	return int(id), nil
}

func (r *ConversionRepository) GetJob(jobID int) (*models.ConversionJob, error) {
	query := `
		SELECT id, user_id, source_path, target_path, source_format, target_format,
			   conversion_type, quality, settings, priority, status, created_at,
			   started_at, completed_at, scheduled_for, duration, error_message
		FROM conversion_jobs
		WHERE id = ?
	`

	job := &models.ConversionJob{}
	var settings, errorMessage sql.NullString
	var startedAt, completedAt, scheduledFor sql.NullTime
	var durationSeconds sql.NullInt64

	err := r.db.QueryRow(query, jobID).Scan(
		&job.ID, &job.UserID, &job.SourcePath, &job.TargetPath, &job.SourceFormat, &job.TargetFormat,
		&job.ConversionType, &job.Quality, &settings, &job.Priority, &job.Status, &job.CreatedAt,
		&startedAt, &completedAt, &scheduledFor, &durationSeconds, &errorMessage)

	if err != nil {
		if err == sql.ErrNoRows {
			return nil, fmt.Errorf("job not found")
		}
		return nil, fmt.Errorf("failed to get job: %w", err)
	}

	if settings.Valid {
		job.Settings = &settings.String
	}

	if errorMessage.Valid {
		job.ErrorMessage = &errorMessage.String
	}

	if startedAt.Valid {
		job.StartedAt = &startedAt.Time
	}

	if completedAt.Valid {
		job.CompletedAt = &completedAt.Time
	}

	if scheduledFor.Valid {
		job.ScheduledFor = &scheduledFor.Time
	}

	if durationSeconds.Valid {
		duration := time.Duration(durationSeconds.Int64) * time.Second
		job.Duration = &duration
	}

	return job, nil
}

func (r *ConversionRepository) UpdateJob(job *models.ConversionJob) error {
	query := `
		UPDATE conversion_jobs
		SET status = ?, started_at = ?, completed_at = ?, duration = ?, error_message = ?, updated_at = ?
		WHERE id = ?
	`

	var startedAt, completedAt sql.NullTime
	var durationSeconds sql.NullInt64
	var errorMessage sql.NullString

	if job.StartedAt != nil {
		startedAt = sql.NullTime{Time: *job.StartedAt, Valid: true}
	}

	if job.CompletedAt != nil {
		completedAt = sql.NullTime{Time: *job.CompletedAt, Valid: true}
	}

	if job.Duration != nil {
		durationSeconds = sql.NullInt64{Int64: int64(job.Duration.Seconds()), Valid: true}
	}

	if job.ErrorMessage != nil {
		errorMessage = sql.NullString{String: *job.ErrorMessage, Valid: true}
	}

	_, err := r.db.Exec(query, job.Status, startedAt, completedAt, durationSeconds, errorMessage, time.Now(), job.ID)
	return err
}

func (r *ConversionRepository) GetUserJobs(userID int, status *string, limit, offset int) ([]models.ConversionJob, error) {
	whereClause := "WHERE user_id = ?"
	args := []interface{}{userID}

	if status != nil {
		whereClause += " AND status = ?"
		args = append(args, *status)
	}

	query := fmt.Sprintf(`
		SELECT id, user_id, source_path, target_path, source_format, target_format,
			   conversion_type, quality, settings, priority, status, created_at,
			   started_at, completed_at, scheduled_for, duration, error_message
		FROM conversion_jobs
		%s
		ORDER BY created_at DESC
		LIMIT ? OFFSET ?
	`, whereClause)

	args = append(args, limit, offset)

	rows, err := r.db.Query(query, args...)
	if err != nil {
		return nil, fmt.Errorf("failed to get user jobs: %w", err)
	}
	defer rows.Close()

	return r.scanJobs(rows)
}

func (r *ConversionRepository) GetJobsByStatus(status string, limit, offset int) ([]models.ConversionJob, error) {
	query := `
		SELECT id, user_id, source_path, target_path, source_format, target_format,
			   conversion_type, quality, settings, priority, status, created_at,
			   started_at, completed_at, scheduled_for, duration, error_message
		FROM conversion_jobs
		WHERE status = ?
		ORDER BY priority DESC, created_at ASC
		LIMIT ? OFFSET ?
	`

	rows, err := r.db.Query(query, status, limit, offset)
	if err != nil {
		return nil, fmt.Errorf("failed to get jobs by status: %w", err)
	}
	defer rows.Close()

	return r.scanJobs(rows)
}

func (r *ConversionRepository) GetStatistics(userID *int, startDate, endDate time.Time) (*models.ConversionStatistics, error) {
	whereClause := "WHERE created_at BETWEEN ? AND ?"
	args := []interface{}{startDate, endDate}

	if userID != nil {
		whereClause += " AND user_id = ?"
		args = append(args, *userID)
	}

	// Get total counts by status
	statusQuery := fmt.Sprintf(`
		SELECT status, COUNT(*) as count
		FROM conversion_jobs
		%s
		GROUP BY status
	`, whereClause)

	statusRows, err := r.db.Query(statusQuery, args...)
	if err != nil {
		return nil, fmt.Errorf("failed to get status statistics: %w", err)
	}
	defer statusRows.Close()

	stats := &models.ConversionStatistics{
		StartDate: startDate,
		EndDate:   endDate,
		ByStatus:  make(map[string]int),
		ByType:    make(map[string]int),
		ByFormat:  make(map[string]int),
	}

	for statusRows.Next() {
		var status string
		var count int
		err := statusRows.Scan(&status, &count)
		if err != nil {
			return nil, fmt.Errorf("failed to scan status stats: %w", err)
		}
		stats.ByStatus[status] = count
		stats.TotalJobs += count
	}

	// Get counts by conversion type
	typeQuery := fmt.Sprintf(`
		SELECT conversion_type, COUNT(*) as count
		FROM conversion_jobs
		%s
		GROUP BY conversion_type
	`, whereClause)

	typeRows, err := r.db.Query(typeQuery, args...)
	if err != nil {
		return nil, fmt.Errorf("failed to get type statistics: %w", err)
	}
	defer typeRows.Close()

	for typeRows.Next() {
		var convType string
		var count int
		err := typeRows.Scan(&convType, &count)
		if err != nil {
			return nil, fmt.Errorf("failed to scan type stats: %w", err)
		}
		stats.ByType[convType] = count
	}

	// Get counts by target format
	formatQuery := fmt.Sprintf(`
		SELECT target_format, COUNT(*) as count
		FROM conversion_jobs
		%s
		GROUP BY target_format
		ORDER BY count DESC
		LIMIT 10
	`, whereClause)

	formatRows, err := r.db.Query(formatQuery, args...)
	if err != nil {
		return nil, fmt.Errorf("failed to get format statistics: %w", err)
	}
	defer formatRows.Close()

	for formatRows.Next() {
		var format string
		var count int
		err := formatRows.Scan(&format, &count)
		if err != nil {
			return nil, fmt.Errorf("failed to scan format stats: %w", err)
		}
		stats.ByFormat[format] = count
	}

	// Get average duration for completed jobs
	durationQuery := fmt.Sprintf(`
		SELECT AVG(duration) as avg_duration
		FROM conversion_jobs
		%s AND status = 'completed' AND duration IS NOT NULL
	`, whereClause)

	var avgDurationSeconds sql.NullFloat64
	err = r.db.QueryRow(durationQuery, args...).Scan(&avgDurationSeconds)
	if err != nil && err != sql.ErrNoRows {
		return nil, fmt.Errorf("failed to get duration statistics: %w", err)
	}

	if avgDurationSeconds.Valid {
		avgDuration := time.Duration(avgDurationSeconds.Float64) * time.Second
		stats.AverageDuration = &avgDuration
	}

	// Calculate success rate
	if completed, ok := stats.ByStatus[models.ConversionStatusCompleted]; ok {
		if failed, failedOk := stats.ByStatus[models.ConversionStatusFailed]; failedOk {
			total := completed + failed
			if total > 0 {
				stats.SuccessRate = float64(completed) / float64(total) * 100
			}
		} else if completed > 0 {
			stats.SuccessRate = 100.0
		}
	}

	return stats, nil
}

func (r *ConversionRepository) CleanupJobs(olderThan time.Time) error {
	query := `
		DELETE FROM conversion_jobs
		WHERE completed_at < ? AND status IN ('completed', 'failed', 'cancelled')
	`

	_, err := r.db.Exec(query, olderThan)
	return err
}

func (r *ConversionRepository) GetActiveJobsCount() (int, error) {
	query := `SELECT COUNT(*) FROM conversion_jobs WHERE status IN ('pending', 'running')`
	var count int
	err := r.db.QueryRow(query).Scan(&count)
	return count, err
}

func (r *ConversionRepository) GetJobsCountByUser(userID int) (int, error) {
	query := `SELECT COUNT(*) FROM conversion_jobs WHERE user_id = ?`
	var count int
	err := r.db.QueryRow(query, userID).Scan(&count)
	return count, err
}

func (r *ConversionRepository) GetPopularFormats(limit int) ([]models.FormatPopularity, error) {
	query := `
		SELECT target_format, COUNT(*) as count
		FROM conversion_jobs
		WHERE status = 'completed'
		GROUP BY target_format
		ORDER BY count DESC
		LIMIT ?
	`

	rows, err := r.db.Query(query, limit)
	if err != nil {
		return nil, fmt.Errorf("failed to get popular formats: %w", err)
	}
	defer rows.Close()

	var formats []models.FormatPopularity
	for rows.Next() {
		var format models.FormatPopularity
		err := rows.Scan(&format.Format, &format.Count)
		if err != nil {
			return nil, fmt.Errorf("failed to scan format popularity: %w", err)
		}
		formats = append(formats, format)
	}

	return formats, nil
}

func (r *ConversionRepository) scanJobs(rows *sql.Rows) ([]models.ConversionJob, error) {
	var jobs []models.ConversionJob

	for rows.Next() {
		var job models.ConversionJob
		var settings, errorMessage sql.NullString
		var startedAt, completedAt, scheduledFor sql.NullTime
		var durationSeconds sql.NullInt64

		err := rows.Scan(
			&job.ID, &job.UserID, &job.SourcePath, &job.TargetPath, &job.SourceFormat, &job.TargetFormat,
			&job.ConversionType, &job.Quality, &settings, &job.Priority, &job.Status, &job.CreatedAt,
			&startedAt, &completedAt, &scheduledFor, &durationSeconds, &errorMessage)

		if err != nil {
			return nil, fmt.Errorf("failed to scan job: %w", err)
		}

		if settings.Valid {
			job.Settings = &settings.String
		}

		if errorMessage.Valid {
			job.ErrorMessage = &errorMessage.String
		}

		if startedAt.Valid {
			job.StartedAt = &startedAt.Time
		}

		if completedAt.Valid {
			job.CompletedAt = &completedAt.Time
		}

		if scheduledFor.Valid {
			job.ScheduledFor = &scheduledFor.Time
		}

		if durationSeconds.Valid {
			duration := time.Duration(durationSeconds.Int64) * time.Second
			job.Duration = &duration
		}

		jobs = append(jobs, job)
	}

	return jobs, nil
}
