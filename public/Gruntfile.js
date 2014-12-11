module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        concat: {
            dist: {
                src: [
                    "javascripts/src/html5shiv.js",
                    "javascripts/src/bootstrap.js",
                    "javascripts/src/angular.js",
                    "javascripts/src/angular-route.js",
                    "javascripts/src/app.js"
                    ],
                dest: 'javascripts/<%= pkg.name %>.min.js'
            },
            css: {
                src: ["stylesheets/src/*.css"],
                dest: "stylesheets/min/<%= pkg.name %>.min.css"
            }
        },
        uglify: {
            options: {
                banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
            },
            dist: {
                files: {
                    'javascripts/<%= pkg.name %>.min.js': ['<%= concat.dist.dest %>']
                }
            }
        },
        qunit: {
            files: ['test/**/*.html']
        },
        jshint: {
            files: ['Gruntfile.js', 'javascripts/src/**/*.js', 'test/**/*.js'],
            options: {
                // options here to override JSHint defaults
                globals: {
                    jQuery: true,
                    console: true,
                    module: true,
                    document: true
                }
            }
        },
        watch: {
            files: ['<%= jshint.files %>', '<%= concat.css.src %>'],
            tasks: ['concat']
        }
    });

    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-qunit');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');

    grunt.registerTask('test', ['jshint', 'qunit']);

    grunt.registerTask('default', ['concat']);
    grunt.registerTask('min', ['concat', 'uglify']);

};
