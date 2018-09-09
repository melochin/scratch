const webpack = require('webpack');

const vendors = [
    'moment',
    'remarkable',
    'react',
    'react-dom',
    'react-router',
    'react-addons-css-transition-group',
    'clipboard',
    'semantic-ui-react',
    '@antv/g2'
];



module.exports = {
    entry: {
        "vendors": vendors
    },
    output: {
        path: __dirname + '/bundle',
        filename: '[name].js',
        library: '[name]',
    },
    plugins: [
        new webpack.DllPlugin({
            path: 'manifest.json',
            name: '[name]',
            context: __dirname
        }),
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /zh-cn/)
    ]
};
