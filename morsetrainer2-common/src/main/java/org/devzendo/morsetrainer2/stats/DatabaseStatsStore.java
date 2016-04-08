package org.devzendo.morsetrainer2.stats;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.h2.engine.ExistenceChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class DatabaseStatsStore implements StatsStore {
	private static Logger LOGGER = LoggerFactory.getLogger(DatabaseStatsStore.class);

	private final File dbFile;
	private final SimpleJdbcTemplate template;
	private final SingleConnectionDataSource dataSource;

	public DatabaseStatsStore(final File storeDir) {
		dbFile = new File(storeDir, "morsetrainer");
		final boolean needToCreate = !exists();
		final String dbURL = "jdbc:h2:" + dbFile.getAbsolutePath();
		final String dbDriverClass = "org.h2.Driver";
		LOGGER.debug("Opening database at {}", dbFile.getAbsolutePath());
		dataSource = new SingleConnectionDataSource(dbDriverClass, dbURL, "sa", "", false);
		template = new SimpleJdbcTemplate(dataSource);
		// Possible Spring bug: if the database isn't there, it doesn't throw
		// an (unchecked) exception. - it does detect it and logs voluminously,
		// but then doesn't pass the error on to me.
		// Looks like a 90013 (DATABASE_NOT_FOUND_1) isn't mapped by the default
		// Spring sql-error-codes.xml.
		// So, I have to check myself. (Obviating one of the reasons I chose
		// Spring!)
//		try {
//			// This'll throw if the db doesn't exist.
//			final boolean closed = dataSource.getConnection().isClosed();
//			LOGGER.debug("db is initially closed? " + closed);
//
//		} catch (final SQLException e) {
//			if (e.getErrorCode() == ErrorCode.DATABASE_NOT_FOUND_1) {
//				LOGGER.warn("Database {} not found", dbFile.getAbsolutePath());
//				create();
//			} else {
//				final String exMessage = "Could not open database - SQL Error Code " + e.getErrorCode();
//				LOGGER.warn("SQLException from isClosed", e);
//				// Assume that anything that goes wrong here is bad...
//				throw new org.springframework.jdbc.UncategorizedSQLException(exMessage, "", e);
//			}
//		}
		if (needToCreate) {
			LOGGER.debug("Database not initialised; creating tables...");
			create();
		} else {
			LOGGER.debug("Database open");
		}
	}

	private boolean exists() {
		return ExistenceChecker.exists(dbFile.getAbsolutePath());
	}

	private void create() {
		LOGGER.info("Creating database...");
		final String[] ddls = new String[] {
			"CREATE TABLE WordLengthStats(length INT, sent INT, success INT)",
			"CREATE TABLE WordLengthPerformance(sessionDate TIMESTAMP, length INT, percentage DOUBLE)",
			"CREATE TABLE CharStats(morseCharacter VARCHAR(15), sent INT, success INT)",
			"CREATE TABLE CharPerformance(sessionDate TIMESTAMP, morseCharacter VARCHAR(15), percentage DOUBLE)",
		};
		for (final String ddl : ddls) {
			template.getJdbcOperations().execute(ddl);
		}
	}

	@Override
	public List<MorseCharacterStat> getStatisticsSortedByAccuracy(final Set<MorseCharacter> morseCharactersSent) {
		final List<MorseCharacterStat> stats = new ArrayList<>();

		final Map<MorseCharacter, Double> percentMap = new HashMap<>();
		for (final MorseCharacter ch : morseCharactersSent) {
			final Double percentage = getMorseCharacterSuccessPercentage(ch);
			percentMap.put(ch, percentage);
		}
		final List<MorseCharacter> sortedKeys = new ArrayList<>(percentMap.keySet());
		sortedKeys.sort(new Comparator<MorseCharacter>() {

			@Override
			public int compare(final MorseCharacter o1, final MorseCharacter o2) {
				if (percentMap.get(o1) < percentMap.get(o2)) {
					return -1;
				} else if (percentMap.get(o1) > percentMap.get(o2)) {
					return 1;
				}
				return 0;
			}
		});
		for (final MorseCharacter key : sortedKeys) {
			stats.add(getMorseCharacterStat(key));
		}

		return stats;
	}

	private MorseCharacterStat getMorseCharacterStat(final MorseCharacter key) {
		return getOptionalMorseCharacterStat(key).orElse(new MorseCharacterStat(key, 0, 0, 0.0));
	}

	private Optional<MorseCharacterStat> getOptionalMorseCharacterStat(final MorseCharacter key) {
		final String sql = "SELECT * FROM CharStats WHERE morseCharacter = ?";
		final List<MorseCharacterStat> query = template.query(sql, new RowMapper<MorseCharacterStat>() {
			@Override
			public MorseCharacterStat mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final int sent = rs.getInt("sent");
				final int success = rs.getInt("success");
				return new MorseCharacterStat(key, sent, success, toPerc(sent, success));
			}
		}, key.toString());
		if (query.isEmpty()) {
			return Optional.<MorseCharacterStat>empty();
		}
		assert(query.size() == 1);
		return Optional.of(query.get(0));
	}

	private Optional<WordLengthStat> getOptionalWordLengthStat(final int length) {
		final String sql = "SELECT * FROM WordLengthStats WHERE length = ?";
		final List<WordLengthStat> query = template.query(sql, new RowMapper<WordLengthStat>() {
			@Override
			public WordLengthStat mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final int sent = rs.getInt("sent");
				final int success = rs.getInt("success");
				return new WordLengthStat(length, sent, success, toPerc(sent, success));
			}
		}, length);
		if (query.isEmpty()) {
			return Optional.<WordLengthStat>empty();
		}
		assert(query.size() == 1);
		return Optional.of(query.get(0));
	}

	protected double toPerc(final int sent, final int success) {
		if (sent == 0) {
			return 0.0;
		}
		return ((double)success / (double)sent) * 100.0;
	}

	@Override
	public void incrementWordLengthSentCount(final int length) {
		final Optional<WordLengthStat> optionalStat = getOptionalWordLengthStat(length);
		if (optionalStat.isPresent()) {
			final WordLengthStat stat = optionalStat.get();
			updateWordLengthStats(length, stat.getNumberSent() + 1, stat.getNumberDecodedCorrectly());
		} else {
			insertWordLengthStats(length, 1, 0);
		}
	}

	private void insertWordLengthStats(final int length, final int sent, final int success) {
		final String sql = "INSERT INTO WordLengthStats (length, sent, success) VALUES (?, ?, ?)";
		template.getJdbcOperations().update(sql, length, sent, success);
	}

	private void updateWordLengthStats(final int length, final int sent, final int success) {
		final String sql = "UPDATE WordLengthStats SET sent = ?, success = ? WHERE length = ?";
		template.update(sql, sent, success, length);
	}

	@Override
	public void incrementWordLengthSuccessCount(final int length) {
		final Optional<WordLengthStat> optionalStat = getOptionalWordLengthStat(length);
		if (optionalStat.isPresent()) {
			final WordLengthStat stat = optionalStat.get();
			updateWordLengthStats(length, stat.getNumberSent(), stat.getNumberDecodedCorrectly() + 1);
		} else {
			insertWordLengthStats(length, 0, 1);
		}
	}

	@Override
	public void incrementSentCount(final MorseCharacter mc) {
		final Optional<MorseCharacterStat> optionalStat = getOptionalMorseCharacterStat(mc);
		if (optionalStat.isPresent()) {
			final MorseCharacterStat stat = optionalStat.get();
			updateCharStats(mc, stat.getNumberSent() + 1, stat.getNumberDecodedCorrectly());
		} else {
			insertCharStats(mc, 1, 0);
		}
	}

	private void insertCharStats(final MorseCharacter mc, final int sent, final int success) {
		final String sql = "INSERT INTO CharStats (morseCharacter, sent, success) VALUES (?, ?, ?)";
		template.getJdbcOperations().update(sql, mc.toString(), sent, success);
	}

	private void updateCharStats(final MorseCharacter mc, final int sent, final int success) {
		final String sql = "UPDATE CharStats SET sent = ?, success = ? WHERE morseCharacter = ?";
		template.update(sql, sent, success, mc.toString());
	}

	@Override
	public void incrementSuccessfulDecodeCount(final MorseCharacter mc) {
		final Optional<MorseCharacterStat> optionalStat = getOptionalMorseCharacterStat(mc);
		if (optionalStat.isPresent()) {
			final MorseCharacterStat stat = optionalStat.get();
			updateCharStats(mc, stat.getNumberSent(), stat.getNumberDecodedCorrectly() + 1);
		} else {
			insertCharStats(mc, 0, 1);
		}
	}

	@Override
	public Double getWordLengthSuccessPercentage(final int wordLength) {
		return getWordLengthStatistics(wordLength).getAccuracyPercentage();
	}

	@Override
	public Double getMorseCharacterSuccessPercentage(final MorseCharacter ch) {
		return getMorseCharacterStat(ch).getAccuracyPercentage();
	}

	@Override
	public WordLengthStat getWordLengthStatistics(final int length) {
		return getOptionalWordLengthStat(length).orElse(new WordLengthStat(length, 0, 0, 0.0));
	}

	@Override
	public void recordWordLengthPerformance(final LocalDateTime when, final int wordLength, final Double percentage) {
		if (wordLengthPerformanceExists(when, wordLength)) {
			updateWordLengthPerformance(when, wordLength, percentage);
		} else {
			insertWordLengthPerformance(when, wordLength, percentage);
		}
	}

	private void insertWordLengthPerformance(final LocalDateTime when, final int wordLength, final Double percentage) {
		final String sql = "INSERT INTO WordLengthPerformance (length, sessionDate, percentage) VALUES (?, ?, ?)";
		template.getJdbcOperations().update(sql, wordLength, when.toString(), percentage);
	}

	private void updateWordLengthPerformance(final LocalDateTime when, final int wordLength, final Double percentage) {
		final String sql = "UPDATE WordLengthPerformance SET percentage = ? WHERE length = ? AND sessionDate = ?";
		template.update(sql, percentage, wordLength, when.toString());
	}

	private boolean wordLengthPerformanceExists(final LocalDateTime when, final int wordLength) {
		return template.queryForInt("SELECT COUNT(*) FROM WordLengthPerformance WHERE length = ? AND sessionDate = ?", wordLength, when.toString()) == 1;
	}

	@Override
	public List<WordLengthPerformance> getWordLengthPerformance(final int wordLength, final LocalDateTime from, final LocalDateTime to) {
		final String sql = "SELECT * FROM WordLengthPerformance WHERE length = ? AND sessionDate BETWEEN ? AND ?";
		return template.query(sql, new RowMapper<WordLengthPerformance>() {
			@Override
			public WordLengthPerformance mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final double perc = rs.getDouble("percentage");
				final Timestamp when = rs.getTimestamp("sessionDate");
				return new WordLengthPerformance(wordLength, when.toLocalDateTime(), perc);
			}
		}, wordLength, from.toString(), to.toString());
	}

	@Override
	public void recordMorseCharacterPerformance(final LocalDateTime when, final MorseCharacter mc, final Double percentage) {
		if (morseCharacterPerformanceExists(when, mc)) {
			updateCharPerformance(when, mc, percentage);
		} else {
			insertCharPerformance(when, mc, percentage);
		}
	}

	private void insertCharPerformance(final LocalDateTime when, final MorseCharacter mc, final Double percentage) {
		final String sql = "INSERT INTO CharPerformance (morseCharacter, sessionDate, percentage) VALUES (?, ?, ?)";
		template.getJdbcOperations().update(sql, mc.toString(), when.toString(), percentage);
	}

	private void updateCharPerformance(final LocalDateTime when, final MorseCharacter mc, final Double percentage) {
		final String sql = "UPDATE CharPerformance SET percentage = ? WHERE morseCharacter = ? AND sessionDate = ?";
		template.update(sql, percentage, mc.toString(), when.toString());
	}

	private boolean morseCharacterPerformanceExists(final LocalDateTime when, final MorseCharacter mc) {
		return template.queryForInt("SELECT COUNT(*) FROM CharPerformance WHERE morseCharacter = ? AND sessionDate = ?", mc.toString(), when.toString()) == 1;
	}

	@Override
	public List<MorseCharacterPerformance> getMorseCharacterPerformance(final MorseCharacter ch, final LocalDateTime from,
			final LocalDateTime to) {
		final String sql = "SELECT * FROM CharPerformance WHERE morseCharacter = ? AND sessionDate BETWEEN ? AND ?";
		return template.query(sql, new RowMapper<MorseCharacterPerformance>() {
			@Override
			public MorseCharacterPerformance mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final double perc = rs.getDouble("percentage");
				final Timestamp when = rs.getTimestamp("sessionDate");
				return new MorseCharacterPerformance(ch, when.toLocalDateTime(), perc);
			}
		}, ch.toString(), from.toString(), to.toString());
	}

	@Override
	public void close() {
		LOGGER.debug("Closing db");
		try {
			 DataSourceUtils.getConnection(dataSource).close();
		} catch (CannotGetJdbcConnectionException | SQLException e) {
			LOGGER.warn("Failed to close db: {}", e.getMessage());
		}
	}
}
