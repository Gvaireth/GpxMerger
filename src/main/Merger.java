package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Merger {

	public static final String IN_PATH = "D://data/gpx/in";
	public static final String OUT_PATH = "D://data/gpx/";
	public static final String HEADER_FILE_PATH = "etc/header.txt";

	public static void main(String[] args) throws IOException {
		List<String> allLines = new ArrayList<String>(100000);
		readHeader(allLines);
		List<Path> files = readGpxFiles();
		appendLines(allLines, files);
		finishFile(allLines);
		writeToFile(allLines);
	}

	private static List<Path> readGpxFiles() throws IOException {
		Path folderPAth = Paths.get(IN_PATH);
		Stream<Path> filesStream = Files.walk(folderPAth, FileVisitOption.FOLLOW_LINKS);
		List<Path> files = filesStream.collect(Collectors.toList());
		filesStream.close();
		return files;
	}

	private static void appendLines(List<String> allLines, List<Path> files) throws IOException {
		for (Path path : files) {
			File file = path.toFile();
			if (file.isDirectory()) {
				System.out.println("Processing directory: " + file.getName());
			} else {
				processFile(file, allLines);
			}
		}
	}

	private static void writeToFile(List<String> allLines) throws IOException, FileNotFoundException {
		String filename = OUT_PATH + new Date().getTime() + ".gpx";
		File all = new File(filename);
		all.createNewFile();
		PrintWriter out = new PrintWriter(filename);
		System.out.println(allLines.size());
		for (String line : allLines) {
			out.println(line);
		}
		out.flush();
		out.close();
	}

	private static void finishFile(List<String> allLines) {
		allLines.add("</trk>");
		allLines.add("</gpx>");
	}

	private static void readHeader(List<String> allLines) throws IOException {
		FileReader fr = new FileReader(HEADER_FILE_PATH);
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			allLines.add(line);
		}
		br.close();
		fr.close();

	}

	private static void processFile(File file, List<String> allLines) throws IOException {
		System.out.println("Processing file: " + file.getName());
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("<trkpt")) {
				allLines.add(line + "</trkpt>");
			}
			if (line.contains("trkseg")) {
				allLines.add(line);
			}
		}
		br.close();
	}
}
