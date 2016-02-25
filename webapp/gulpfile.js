'use strict';

// Gulp File
// Used to automate Web Applicaiton building, minimisation & releasing

var gulp = require('gulp');
var watch = require('gulp-watch');
var liveReload  = require('gulp-livereload');
var clean = require('gulp-clean');
var jshint = require('gulp-jshint');
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


var bases = {
    app: 'app/', // Source directory
    dist: 'dist/' // Compiled, minified target directory
};




// === TASKS ===

// Delete the dist directory
gulp.task('clean', function() {
    return gulp.src(bases.dist)
        .pipe(clean());
});

// Relplace all js + css references in index.html with minified and compressed ones.
gulp.task('usemin', function() {
    return gulp.src(bases.app + 'index.html')
        .pipe(usemin({
            mainCss: [ rev ],
            vendorCss: [ rev ],
            html: [ function () {return minifyHtml({ empty: true });} ],
            appJs: [ ngAnnotate, uglify, rev ], // ngAnnotate
            vendorJs: [ ngAnnotate, uglify, rev ] // ngAnnotate
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


/*
 * Wire-up the bower dependencies
 * @return {Stream}

gulp.task('wiredep', function() {
    log('Wiring the bower dependencies into the html');

    var wiredep = require('wiredep').stream;
    var options = config.getWiredepDefaultOptions();

    // Only include stubs if flag is enabled
    var js = args.stubs ? [].concat(config.js, config.stubsjs) : config.js;

    return gulp
        .src(config.index)
        .pipe(wiredep(options))
        .pipe(inject(js, '', config.jsOrder))
        .pipe(gulp.dest(config.client));
});
 */
/*
gulp.task('inject', ['wiredep', 'styles', 'templatecache'], function() {
    log('Wire up css into the html, after files are ready');

    return gulp
        .src(config.index)
        .pipe(inject(config.css))
        .pipe(gulp.dest(config.client));
});
*/


// A development task to run anytime a file changes

gulp.task('watch', function() {
    gulp.watch('app/**/*', ['scripts', 'copy']);
});

// Define the default task as a sequence of the above tasks
gulp.task('default', ['build']);

gulp.task('build', ['clean', 'usemin', 'copy']);
