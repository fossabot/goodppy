package org.goodppy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ビルドが正しく行えるかをチェックするクラス
 */
public class BuildChecker {

	/**
	 * ログファイルの
	 */
	private String logFileDirectory;

	/**
	 * コンストラクタ
	 */
	public BuildChecker(String repositoryUrl) {
		RepositoryController repositoryController = new RepositoryController(repositoryUrl);
		this.logFileDirectory = "./logs/"
				+ repositoryController.getOwner()
				+ "/"
				+ repositoryController.getRepositoryName()
				+ "/";
	}

	/**
	 * ビルドが行えるかをチェックする
	 * 
	 * @param localPath クローン先のディレクトリのパス
	 */
	public void buildCheck(String localPath, String repositoryUrl) {
		try {
			System.out.println("Start the build...");
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("gradle", "build"); // Gradleのビルドを行う
			builder.directory(new File(localPath));
			createFile(repositoryUrl);
			builder.redirectErrorStream(true);
			builder.redirectOutput(generateLogfile(repositoryUrl).toFile());
			Process process = builder.start();
			long start = System.nanoTime();
			Integer exitCode = process.waitFor();
			if (exitCode != 0) {
				System.out.println("Build failed");
				long end = System.nanoTime();
				System.out.println("Time taken to build : " + (end - start) + "ns");
				return;
			}
			System.out.println("Build success");
			long end = System.nanoTime();
			System.out.println("Time taken to build : " + (end - start) + "ns");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	public void createFile(String repositoryUrl) {
		File logDirectory = new File(getLogfileDirectory());
		File logFile = new File(generateLogfile(repositoryUrl).toString());
		try {
			if (logDirectory.exists() == false) {
				logDirectory.mkdirs();
			}
			if (logFile.exists() == false)
				logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public Path generateLogfile(String repositoryUrl) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		// RepositoryController repositoryController = new RepositoryController(repositoryUrl);
		String logFile = getLogfileDirectory()
				// + repositoryController.getOwner()
				// + "_"
				// + repositoryController.getRepositoryName()
				// + "_"
				+ sdf.format(calendar.getTime())
				+ ".log";
		Path logFilePath = Paths.get(logFile);
		return logFilePath;
	}

	public String getLogfileDirectory() {
		return this.logFileDirectory;
	}
}
