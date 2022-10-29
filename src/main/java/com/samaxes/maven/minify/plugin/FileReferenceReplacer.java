package com.samaxes.maven.minify.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

public class FileReferenceReplacer {
  private static final Pattern filePattern =
      Pattern.compile("([\"']([^\"']*?/)?)([^/\"']*?\\.(?:js|css))((?:\\?[^\"']*)?[\"'])");

  private Map<String, String> fileNames;

  private String baseTargetPath;

  private boolean verbose;

  private Log log;

  private String jsSourceDir;

  private String cssSourceDir;

  private String htmlSourceDir;

  private String charset;

  public FileReferenceReplacer(Map<String, String> fileNames, String baseTargetPath,
      boolean verbose, Log log, String jsSourceDir, String cssSourceDir, String htmlSourceDir, String charset) {
    super();
    this.fileNames = fileNames;
    this.baseTargetPath = baseTargetPath;
    this.verbose = verbose;
    this.log = log;
    this.jsSourceDir = jsSourceDir;
    this.cssSourceDir = cssSourceDir;
    this.htmlSourceDir = htmlSourceDir;
    this.charset = charset;
  }

  public void process() throws IOException {
    System.out.println(fileNames);
    for (Entry<String, String> entry : fileNames.entrySet()) {
      StringBuffer newResult = new StringBuffer();
      File minifiedFile =
          new File(new File(baseTargetPath + entry.getKey()).getParentFile(), entry.getValue());
      try (InputStream in = new FileInputStream(minifiedFile)) {
        log.info("Replacing references in ["
            + (verbose ? minifiedFile.getPath() : minifiedFile.getName()) + "].");

        String original = IOUtil.toString(in, charset);
        Matcher matcher = filePattern.matcher(original);
        while (matcher.find()) {
          String filename = ObjectUtils.firstNonNull(matcher.group(2), "") + matcher.group(3);
          String referencedPath = new File(
              (filename.startsWith("/") ? getBasePath(filename) : (minifiedFile.getParent() + "/"))
                  + filename).getAbsolutePath().substring(baseTargetPath.length());
          String newName = fileNames.get(referencedPath);
          if (newName == null) {
            log.info("Did not find replacement for " + referencedPath);
            matcher.appendReplacement(newResult, matcher.group());
          } else {
            log.info("Replacing " + filename + " with "
                + ObjectUtils.firstNonNull(matcher.group(2), "") + newName);
            matcher.appendReplacement(newResult, "$1" + newName + "$4");
          }
        }
        matcher.appendTail(newResult);
      } catch (IOException e) {
        log.error("Failed to replace references in file ["
            + (verbose ? minifiedFile.getPath() : minifiedFile.getName()) + "].", e);
        throw e;
      }
      try (OutputStream out = new FileOutputStream(minifiedFile)) {
        IOUtil.copy(newResult.toString().getBytes(charset), out);
      } catch (IOException e) {
        log.error("Failed to replace references in file ["
            + (verbose ? minifiedFile.getPath() : minifiedFile.getName()) + "].", e);
        throw e;
      }
    }
  }

  private String getBasePath(String filename) {
    switch (filename.substring(filename.lastIndexOf('.'))) {
      case ".js":
        return baseTargetPath + "/" + jsSourceDir;
      case ".css":
        return baseTargetPath + "/" + cssSourceDir;
      case ".html":
        return baseTargetPath + "/" + htmlSourceDir;
      default:
        return baseTargetPath;
    }
  }

}
