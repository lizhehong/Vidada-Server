'use strict';

//-----------------------------------------------------------------------
// Gulp File
// Used to automate Web Applicaiton building, minimisation & releasing
// 
// Tasks
// --------
// compile :   Builds the web app locally, compiles css, updates index.html dependencies
// release-prod:  Minifies all css/js dependencies, bundles the production ready with the backend
//                (Copys the minified web-app into the backends '/src/main/resources/static')
//
// release-dev:  Copy all source files into backend bundle folder, don't uglyfi
//-----------------------------------------------------------------------


// Dependencies (require a previous 'npm install')

var gulp = require('gulp');

var gutil = require('gulp-util');
var clean = require('gulp-clean');
var concat = require('gulp-concat');
var usemin = require('gulp-usemin');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var moment = require('moment');
var notify  = require('gulp-notify');
var minifyHtml = require('gulp-minify-html');
var minifyCss = require('gulp-minify-css');
var rev = require('gulp-rev');
var ngAnnotate = require('gulp-ng-annotate');
var wiredep = require('wiredep').stream;
var inject = require('gulp-inject');
var naturalSort = require('gulp-natural-sort');
var angularFilesort = require('gulp-angular-filesort');

var jshint = require('gulp-jshint');
var watch = require('gulp-watch');
var liveReload  = require('gulp-livereload');



// Configuration

var bases = {
    app: 'app/', // Source directory
    dist: '../vidada-server/src/main/resources/static/' // Compiled, minified target directory
};


var paths = {
    index : bases.app + 'index.html',
    localJs : bases.app + 'scripts/**/*.js',
    localCss : bases.app + 'assets/styles/**/*.css'
}

// === TASKS ===

// Delete the dist directory
gulp.task('clean', function() {
    return gulp.src(bases.dist, {read: false})
        .pipe( clean( { force: true } ) );
});

/**
 * Relplace all js + css references in index.html with minified and compressed ones.
 */
gulp.task('usemin', ['clean', 'build'], function() {
    return gulp.src(bases.app + 'index.html')
        .pipe(usemin({
            mainCss: [ rev ],
            vendorCss: [ rev ],
            html: [ function () {return minifyHtml({ empty: true });} ],
            appJs: [ ngAnnotate, uglify, rev ],
            vendorJs: [ ngAnnotate, uglify, rev ]
        }))
        .pipe(gulp.dest(bases.dist));
});

/**
 * Simply merge all dependencies in index.html into min-dependencies.
 * FOR DEVELOPMENT ONLY - does not uglify / minimize or compress
 */
gulp.task('dev-dependencies', ['clean', 'build'], function() {
    return gulp.src(bases.app + 'index.html')
        .pipe(usemin({
            mainCss: [ rev ],
            vendorCss: [ rev ],
            appJs: [ rev ],
            vendorJs: [ rev ]
        }))
        .pipe(gulp.dest(bases.dist));
});




// Copy all other files to dist directly
gulp.task('copy', ['clean'], function() {
    // Copy html
    gulp.src(bases.app + 'scripts/**/*.html')
        .pipe(gulp.dest(bases.dist + 'scripts'));

    // Copy images
    gulp.src(bases.app + 'assets/images/**/*.png')
        .pipe(gulp.dest(bases.dist+'assets/images/'));

    // Copy i18n
    gulp.src(bases.app + 'i18n/**/*.json')
        .pipe(gulp.dest(bases.dist+'i18n/'));

    // Copy extra html5bp files
    gulp.src([bases.app + 'robots.txt', bases.app + 'favicon.ico', bases.app + '*.json'])
        .pipe(gulp.dest(bases.dist));
});


// Inject all dependeincies into index.html
gulp.task('inject', ['inject-bower', 'inject-local-css', 'inject-local-js']);

/**
 * Wire-up bower dependencies automatically (js + css)
 */
gulp.task('inject-bower', function () {
  return gulp.src(paths.index)
    .pipe(wiredep())
    .pipe(gulp.dest(bases.app)); // In place update
});


gulp.task('inject-local-css', ['inject-bower'], function() {
    // // Inject CSS
    return gulp.src(paths.index)
        .pipe(inject(gulp.src(paths.localCss, {read: false})
            .pipe(naturalSort())
            ,
            // Inject Options
         { 
            relative: true
         }))
        .pipe(gulp.dest(bases.app)); // In place update
});


gulp.task('inject-local-js', ['inject-local-css'], function() {
    // // Inject JS
      
    // It's not necessary to read the files (will speed up things), we're only after their paths:
    return gulp.src(paths.index)
        .pipe(inject(gulp.src(paths.localJs) // {read: false} - read required by angular file-sort
            .pipe(naturalSort())
            .pipe(angularFilesort())
            ,
            // Inject Options
         { 
            relative: true
         }))
        .pipe(gulp.dest(bases.app)); // In place update
});





/**
 * A development task to run anytime a file changes
 */
gulp.task('watch', function() {
    //gulp.watch(bases.app + '**/*', ['scripts', 'copy']);
});


gulp.task('compile', ['inject']);
gulp.task('release-prod', ['clean', 'build', 'usemin', 'copy']);
gulp.task('release-dev', ['clean', 'build', 'dev-dependencies', 'copy']);

/**
 * Default task, executed if no specific task is specified.
 */
gulp.task('default', function() {
    gutil.log(gutil.colors.red('No task defined. Please choose one of the following tasks:'));

    gutil.log(gutil.colors.cyan('compile:'),' Builds all SAAS etc. and injects all dependencies into index.html.');
    gutil.log(gutil.colors.cyan('release-dev:'),' Executes build and copies the webapp into the java resource folder.')
    gutil.log(gutil.colors.cyan('release-prod:'),' Executes build and copies the minified/compressed webapp into the java resource folder.')
});
