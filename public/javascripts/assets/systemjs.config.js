/* global SystemJS */

/**
 * System configuration for Angular samples
 * Adjust as necessary for your application needs.
 */
(function (global) {
    SystemJS.config({
        paths: {
            // paths serve as alias
            'npm:': '/assets/lib/',

            // our app is within the app folder
            'app': '/assets/app',
            'jointjs': '/assets/lib/jointjs/dist/joint.js'
        },
        // map tells the System loader where to look for things
        map: {
            'bootstrap': 'npm:bootstrap/dist/js/bootstrap.js',
            'popper.js': 'npm:popper.js/umd/popper.js',

            'codemirror': 'npm:codemirror',

            'graphlib': 'npm:graphlib/dist/graphlib.js',
            'jquery': 'npm:jquery/dist/jquery.js',
            'backbone': 'npm:backbone/backbone.js',

            'lodash': 'npm:lodash/lodash.js',
            'underscore': 'npm:lodash/lodash.js',

            'dagre': 'npm:dagre/dist/dagre.js',

            'jointjs': {
                'backbone': '/assets/app/jointjs-loader-hack.js',
            }
        },
        // packages tells the System loader how to load when no filename and/or no extension
        packages: {
            app: {
                defaultExtension: 'js'
            },
            '/assets/lib': {
                defaultExtension: 'js'
            },
            codemirror: {
                main: 'lib/codemirror.js'
            }
        }
    });
})(this);