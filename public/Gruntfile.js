module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        concat: {
            main: {
                src: [
                    "javascripts/libs/bootstrap.js",
                    "javascripts/libs/angular.js",
                    "javascripts/libs/angular-route.js",
                    "javascripts/libs/angular-match.js",
                    "javascripts/src/app.js",
					"javascripts/src/services/*.js",
					"javascripts/src/controllers/*.js",
					"javascripts/src/directives/*.js",
					"javascripts/src/filters/*.js",
					"javascripts/libs/jquery-ui.custom.js",
					"javascripts/libs/jquery.nanoscroller.js",
					"javascripts/libs/ng-table.js",
					"javascripts/libs/jquery.maskedinput.min.js",
					"javascripts/libs/bootstrap-datepicker.js",
					"javascripts/libs/bootstrap-timepicker.js",
					"javascripts/src/scripts.js"
                    ],
                dest: 'javascripts/min/<%= pkg.name %>.min.js'
            },
			headlibs: {
				src: ["javascripts/libs/jquery.js", "javascripts/libs/html5shiv.js","javascripts/libs/modernizr.js"],
				dest: "javascripts/min/headlibs.min.js"
			},

            css: {
				src: [ "scss/compiled/theme.css", "stylesheets/libs/*.css", "stylesheets/src/*.css"],
				dest: "stylesheets/min/<%= pkg.name %>.min.css"
            },
			bootstrap: {
				src: ["scss/compiled/bootstrap.css"],
				dest: "stylesheets/min/bootstrap.min.css"
			}
        },
		sass: {                              // Task
			dist: {                            // Target
				options: {                       // Target options
					style: 'expanded'
				},
				files: {                         // Dictionary of files
					'scss/compiled/theme.css': 'scss/theme/theme_styles.scss',
					'scss/compiled/bootstrap.css': 'scss/bootstrap.scss'
				}
			}
		},
        uglify: {
            dist: {
                files: {
                    'javascripts/min/<%= pkg.name %>.min.js': ['<%= concat.main.dest %>'],
					'javascripts/min/headlibs.min.js': ['<%= concat.headlibs.dest %>']
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
        },
		cssmin: {
			target: {
				files: {
					'<%= concat.css.dest %>': ['<%= concat.css.dest %>'],
					'<%= concat.bootstrap.dest %>': ['<%= concat.bootstrap.dest %>']
				}
			}
		}
    });

    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-qunit');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-sass');
	grunt.loadNpmTasks('grunt-contrib-cssmin');

    grunt.registerTask('test', ['jshint', 'qunit']);

    grunt.registerTask('default', ['sass','concat']);
    grunt.registerTask('min', ['sass', 'concat', 'uglify', 'cssmin']);

};
