const webpack = require('webpack');

const vendors = [
    'moment',
    'remarkable',
    'react-addons-css-transition-group',
    'clipboard',
    'semantic-ui-react'
];

module.exports = {
    entry: {
        "vendors": vendors,
    },
    output: {
        path: 'bundle',
        filename: '[name].js',
        library: '[name]',
    },
    plugins: [
        new webpack.DllPlugin({
            path: 'manifest.json',
            name: '[name]',
            context: __dirname,
        }),
        new webpack.optimize.UglifyJsPlugin({
            compress: {
                warnings: false
            }
        }),
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /zh-cn/)
    ]
};