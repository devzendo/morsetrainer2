package org.devzendo.morsetrainer2.mp3;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.util.Optional;

import org.junit.Test;

public class TestConversion {

	@Test
	public void emptyIsPreservedByToWav() {
		final Optional<File> noRecordFile = Optional.<File>empty();
		assertThat(Conversion.toWav(noRecordFile), equalTo(Optional.empty()));
	}

	@Test
	public void presentMp3GetsTranslatedByToWav() {
		final Optional<File> mp3RecordFile = Optional.<File>of(new File("foo.mp3"));
		assertThat(Conversion.toWav(mp3RecordFile), equalTo(Optional.of(new File("foo.wav"))));
	}

	@Test
	public void presentWavStaysByToWav() {
		final Optional<File> wavRecordFile = Optional.<File>of(new File("foo.wav"));
		assertThat(Conversion.toWav(wavRecordFile), equalTo(Optional.of(new File("foo.wav"))));
	}

	@Test
	public void emptyIsPreservedByToOptionalMP3() {
		final Optional<File> noRecordFile = Optional.<File>empty();
		assertThat(Conversion.toMP3(noRecordFile), equalTo(Optional.empty()));
	}

	@Test
	public void presentWavGetsTranslatedByToOptionalMP3() {
		final Optional<File> wavRecordFile = Optional.<File>of(new File("foo.wav"));
		assertThat(Conversion.toMP3(wavRecordFile), equalTo(Optional.of(new File("foo.mp3"))));
	}

	@Test
	public void presentMP3StaysByToOptionalMP3() {
		final Optional<File> mp3RecordFile = Optional.<File>of(new File("foo.mp3"));
		assertThat(Conversion.toMP3(mp3RecordFile), equalTo(Optional.of(new File("foo.mp3"))));
	}

	@Test
	public void presentWavGetsTranslatedByToMP3() {
		final File wavRecordFile = new File("foo.wav");
		assertThat(Conversion.toMP3(wavRecordFile), equalTo(new File("foo.mp3")));
	}

	@Test
	public void presentMP3StaysByToMP3() {
		final File mp3RecordFile = new File("foo.mp3");
		assertThat(Conversion.toMP3(mp3RecordFile), equalTo(new File("foo.mp3")));
	}

	@Test
	public void isWavWithLowerCase() {
		assertThat(Conversion.isWav(new File("/tmp/foo.wav")), equalTo(true));
		assertThat(Conversion.isWav(new File("foo.wav")), equalTo(true));

		assertThat(Conversion.isWav(new File("foo.Wav")), equalTo(false));
		assertThat(Conversion.isWav(new File("/tmp/foo.mp3")), equalTo(false));
		assertThat(Conversion.isWav(new File("foo.MP3")), equalTo(false));
		assertThat(Conversion.isWav(new File("foo.mp3")), equalTo(false));
	}

	@Test
	public void isMP3WithLowerCase() {
		assertThat(Conversion.isMP3(new File("/tmp/foo.mp3")), equalTo(true));
		assertThat(Conversion.isMP3(new File("foo.mp3")), equalTo(true));

		assertThat(Conversion.isMP3(new File("foo.MP3")), equalTo(false));
		assertThat(Conversion.isMP3(new File("/tmp/foo.wav")), equalTo(false));
		assertThat(Conversion.isMP3(new File("foo.Wav")), equalTo(false));
		assertThat(Conversion.isMP3(new File("foo.wav")), equalTo(false));
	}

	@Test
	public void isOptionalWavWithLowerCase() {
		assertThat(Conversion.isWav(Optional.of(new File("/tmp/foo.wav"))), equalTo(true));
		assertThat(Conversion.isWav(Optional.of(new File("foo.wav"))), equalTo(true));

		assertThat(Conversion.isWav(Optional.empty()), equalTo(false));

		assertThat(Conversion.isWav(Optional.of(new File("foo.Wav"))), equalTo(false));
		assertThat(Conversion.isWav(Optional.of(new File("/tmp/foo.mp3"))), equalTo(false));
		assertThat(Conversion.isWav(Optional.of(new File("foo.MP3"))), equalTo(false));
		assertThat(Conversion.isWav(Optional.of(new File("foo.mp3"))), equalTo(false));
	}

	@Test
	public void isOptionalMP3WithLowerCase() {
		assertThat(Conversion.isMP3(Optional.of(new File("/tmp/foo.mp3"))), equalTo(true));
		assertThat(Conversion.isMP3(Optional.of(new File("foo.mp3"))), equalTo(true));

		assertThat(Conversion.isMP3(Optional.empty()), equalTo(false));

		assertThat(Conversion.isMP3(Optional.of(new File("foo.MP3"))), equalTo(false));
		assertThat(Conversion.isMP3(Optional.of(new File("/tmp/foo.wav"))), equalTo(false));
		assertThat(Conversion.isMP3(Optional.of(new File("foo.Wav"))), equalTo(false));
		assertThat(Conversion.isMP3(Optional.of(new File("foo.wav"))), equalTo(false));
	}

}
