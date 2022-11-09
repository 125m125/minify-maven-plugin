This fork minifies html and replaces references to the original files with references to the minified files. I only tested it with my specific configuration and it will probably not work with others (at least combining files has to be disabled etc.)

To use the fork, clone it locally, `mvn clean install` it locally and use

```xml
<groupId>com.samaxes.maven</groupId>
<artifactId>minify-maven-plugin</artifactId>
<version>2.0.0-SNAPSHOT-125m125</version>
```

HTML files have the same options as javascript etc (`htmlSourceDir`, `htmlSourceIncludes`, ...).

HTML files won't get a suffix. `[hash]` in the `<suffix>` will be replaced with the md5 hash of the source file for cache busting.

New features were only quickly added in without worrying about maintainability or performance and mostly to simply get it to work with my project. I would not recommend using it for actual production or larger projects.

The following configuration works for me with html, css and js all placed directly inside `src/main/resources/web` and some subfolders. Your mileage may vary:

```xml
<plugin>
	<groupId>com.samaxes.maven</groupId>
	<artifactId>minify-maven-plugin</artifactId>
	<version>2.0.0-SNAPSHOT-125m125</version>
	<executions>
		<execution>
			<id>default-minify</id>
			<configuration>
				<jsEngine>CLOSURE</jsEngine>
				<skipMerge>true</skipMerge>
				<suffix>.[hash]</suffix>
				<charset>${project.build.sourceEncoding}</charset>
				<webappSourceDir>${project.basedir}/src/main/resources/web</webappSourceDir>
            	<webappTargetDir>${project.build.outputDirectory}/web</webappTargetDir>
				<cssSourceDir>./</cssSourceDir>
				<jsSourceDir>./</jsSourceDir>
				<htmlSourceDir>./</htmlSourceDir>
				<cssSourceIncludes>
					<cssSourceInclude>**/*.css</cssSourceInclude>
				</cssSourceIncludes>
				<cssSourceExcludes>
					<cssSourceExclude>**/*.min.css</cssSourceExclude>
				</cssSourceExcludes>
				<jsSourceIncludes>
					<jsSourceInclude>**/*.js</jsSourceInclude>
				</jsSourceIncludes>
				<jsSourceExcludes>
					<jsSourceExclude>**/*.min.js</jsSourceExclude>
				</jsSourceExcludes>
				<htmlSourceIncludes>
					<htmlSourceInclude>**/*.html</htmlSourceInclude>
				</htmlSourceIncludes>
			</configuration>
			<goals>
				<goal>minify</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```



[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.samaxes.maven/minify-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.samaxes.maven/minify-maven-plugin)
[![Build Status](https://travis-ci.org/samaxes/minify-maven-plugin.png)](https://travis-ci.org/samaxes/minify-maven-plugin)

# Minify Maven Plugin

Minify Maven Plugin combines and minimizes your CSS and JavaScript files for faster page loading. It produces a merged and a minified version of your CSS and JavaScript resources which can be re-used across your project.

Under the hood, it uses the [YUI Compressor](http://yui.github.com/yuicompressor/) and [Google Closure Compiler](https://developers.google.com/closure/compiler/) but has a layer of abstraction around these tools which allows for other tools to be added in the future.

## Benefits

### Reduce HTTP Requests

> 80% of the end-user response time is spent on the front-end. Most of this time is tied up in downloading all the components in the page: images, stylesheets, scripts, etc. Reducing the number of components in turn reduces the number of HTTP requests required to render the page. This is the key to faster pages.
>
> Combined files are a way to reduce the number of HTTP requests by combining all scripts into a single script, and similarly combining all CSS into a single stylesheet. Combining files is more challenging when the scripts and stylesheets vary from page to page, but making this part of your release process improves response times.

### Compress JavaScript and CSS

> Minification/compression is the practice of removing unnecessary characters from code to reduce its size thereby improving load times. A JavaScript compressor, in addition to removing comments and white-spaces, obfuscates local variables using the smallest possible variable name. This improves response time performance because the size of the downloaded file is reduced.

## Usage

Configure your project's `pom.xml` to run the plugin during the project's build cycle.

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.samaxes.maven</groupId>
      <artifactId>minify-maven-plugin</artifactId>
      <version>1.7.6</version>
      <executions>
        <execution>
          <id>default-minify</id>
          <configuration>
            <charset>UTF-8</charset>
            <cssSourceFiles>
              <cssSourceFile>file-1.css</cssSourceFile>
              <!-- ... -->
              <cssSourceFile>file-n.css</cssSourceFile>
            </cssSourceFiles>
            <jsSourceFiles>
              <jsSourceFile>file-1.js</jsSourceFile>
              <!-- ... -->
              <jsSourceFile>file-n.js</jsSourceFile>
            </jsSourceFiles>
            <jsEngine>CLOSURE</jsEngine>
          </configuration>
          <goals>
            <goal>minify</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Notice that the [v1.6.X branch](https://github.com/samaxes/minify-maven-plugin/tree/v1.6.X) contains the last Java 6 compatible releases. Java 7 is required for new versions.  
For more information, check the [documentation](http://samaxes.github.com/minify-maven-plugin/) or the [demo applications](https://github.com/samaxes/minify-maven-plugin/releases/).

## License

This distribution is licensed under the terms of the Apache License, Version 2.0 (see LICENSE.txt).
