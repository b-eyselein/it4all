/**
 * System configuration for Angular samples
 * Adjust as necessary for your application needs.
 */
(function (global) {
    SystemJS.config({
        paths: {
            // paths serve as alias
            'npm:': '/assets/lib/'
        },
        // map tells the System loader where to look for things
        map: {
            // our app is within the app folder
            'app': '/assets/app',

            'graphlib': 'npm:graphlib/dist/graphlib.js',
            'jquery': 'npm:jquery/jquery.js',
            'backbone': 'npm:backbone/backbone.js',

            'lodash': 'npm:lodash/lodash.js',
            'underscore': 'npm:lodash/lodash.js',

            'dagre': 'npm:dagre/dist/dagre.js',
            'jointjs': 'npm:jointjs/dist/joint.js'
        },
        // packages tells the System loader how to load when no filename and/or no extension
        packages: {
            app: {
                defaultExtension: 'js'
            }
        }
    });
})(this);