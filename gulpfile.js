'use strict';

// Gulp File
// Used to automate Web Applicaiton building, minimisation & releasing

var gulp = require('gulp');
var clean = require('gulp-clean');
var jshint = require('gulp-jshint');
var concat = require('gulp-concat');
var usemin = require('gulp-usemin');
var uglify = require('gulp-uglify');
var minifyHtml = require('gulp-minify-html');
var minifyCss = require('gulp-minify-css');
var rev = require('gulp-rev');


var bases = {
    app: 'src/main/resources/static/',
    dist: 'src/main/resources/static/dist/'
};

var paths = {
    //styles: [bases.app + 'assets/styles/**/*.css'],
    html: [bases.app + 'scripts/**/*.html'],
    images: [bases.app + 'assets/images/**/*.png', 'images/**/*.png'],
    extras: [bases + 'robots.txt', 'favicon.ico']
};


// === TASKS ===

// Delete the dist directory
gulp.task('clean', function() {
    return gulp.src(bases.dist)
        .pipe(clean());
});


gulp.task('usemin', function() {
    return gulp.src(bases.app + 'index.html')
        .pipe(usemin({
            css: [ rev ],
            html: [ function () {return minifyHtml({ empty: true });} ],
            js: [ uglify, rev ],
            inlinejs: [ uglify ],
            inlinecss: [ minifyCss, 'concat' ]
        }))
        .pipe(gulp.dest(bases.dist));
});



// Copy all other files to dist directly
gulp.task('copy', ['clean'], function() {
    // Copy html
    gulp.src(paths.html)
        .pipe(gulp.dest(bases.dist));

    // Copy extra html5bp files
    gulp.src(paths.extras)
        .pipe(gulp.dest(bases.dist));
});

// A development task to run anytime a file changes
gulp.task('watch', function() {
    gulp.watch('app/**/*', ['scripts', 'copy']);
});

// Define the default task as a sequence of the above tasks
gulp.task('default', ['build']);

gulp.task('build', ['clean', 'usemin', 'copy']);
