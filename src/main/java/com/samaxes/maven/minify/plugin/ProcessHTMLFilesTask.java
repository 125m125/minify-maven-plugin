/*
 * Minify Maven Plugin
 * https://github.com/samaxes/minify-maven-plugin
 *
 * Copyright (c) 2009 samaxes.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.samaxes.maven.minify.plugin;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.samaxes.maven.minify.common.YuiConfig;
import com.samaxes.maven.minify.plugin.MinifyMojo.Engine;
import com.yahoo.platform.yui.compressor.CssCompressor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Task for merging and compressing CSS files.
 */
public class ProcessHTMLFilesTask extends ProcessFilesTask {

  HtmlCompressor htmlCompressor = new HtmlCompressor();
  
    /**
     * Task constructor.
     *
     * @param log             Maven plugin log
     * @param verbose         display additional info
     * @param bufferSize      size of the buffer used to read source files
     * @param charset         if a character set is specified, a byte-to-char variant allows the encoding to be selected.
     *                        Otherwise, only byte-to-byte operations are used
     * @param suffix          final file name suffix
     * @param nosuffix        whether to use a suffix for the minified file name or not
     * @param skipMerge       whether to skip the merge step or not
     * @param skipMinify      whether to skip the minify step or not
     * @param webappSourceDir web resources source directory
     * @param webappTargetDir web resources target directory
     * @param inputDir        directory containing source files
     * @param sourceFiles     list of source files to include
     * @param sourceIncludes  list of source files to include
     * @param sourceExcludes  list of source files to exclude
     * @param outputDir       directory to write the final file
     * @param outputFilename  the output file name
     * @param engine          minify processor engine selected
     * @param yuiConfig       YUI Compressor configuration
     * @param newNames 
     * @throws FileNotFoundException when the given source file does not exist
     */
    public ProcessHTMLFilesTask(Log log, boolean verbose, Integer bufferSize, Charset charset, String suffix,
                               boolean nosuffix, boolean skipMerge, boolean skipMinify, String webappSourceDir,
                               String webappTargetDir, String inputDir, List<String> sourceFiles,
                               List<String> sourceIncludes, List<String> sourceExcludes, String outputDir,
                               String outputFilename, Engine engine, YuiConfig yuiConfig, Map<String, String> newNames) throws FileNotFoundException {
        super(log, verbose, bufferSize, charset, suffix, true, true, skipMinify, webappSourceDir,
                webappTargetDir, inputDir, sourceFiles, sourceIncludes, sourceExcludes, outputDir, outputFilename,
                engine, yuiConfig, newNames);
    }

    /**
     * Minifies a CSS file. Create missing parent directories if needed.
     *
     * @param mergedFile   input file resulting from the merged step
     * @param minifiedFile output file resulting from the minify step
     * @throws IOException when the minify step fails
     */
    @Override
    protected void minify(File mergedFile, File minifiedFile) throws IOException {
        if (!minifiedFile.getParentFile().exists() && !minifiedFile.getParentFile().mkdirs()) {
            throw new RuntimeException("Unable to create target directory for: " + minifiedFile.getParentFile());
        }

        try (InputStream in = new FileInputStream(mergedFile);
             OutputStream out = new FileOutputStream(minifiedFile)) {
            log.info("Creating the minified file [" + (verbose ? minifiedFile.getPath() : minifiedFile.getName())
                    + "].");

            log.debug("Using HtmlCompressor.");
            String result = htmlCompressor.compress(IOUtil.toString(in, charset.name()));
            IOUtil.copy(result.getBytes(charset), out);
        } catch (IOException e) {
            log.error("Failed to compress the HTML file [" + (verbose ? mergedFile.getPath() : mergedFile.getName())
                    + "].", e);
            throw e;
        }

        logCompressionGains(mergedFile, minifiedFile);
    }
}
