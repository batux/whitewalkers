
var configurationOfRequireJS = {
	
    baseUrl: 'js',
	paths : {
		'jquery': 'jquery',
		'knockout':'knockout',
		'async': 'async',
		'text':'text',
		'underscore':'underscore-min',
		'bootstrap':'bootstrap.min'
	},
	shim: {
		'bootstrap': ['jquery']
	}

}

require.config(configurationOfRequireJS);

require(['knockout','text']);


