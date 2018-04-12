var webpack = require("webpack");
var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var CompressionWebpackPlugin = require('compression-webpack-plugin');

module.exports = {
  entry: {
    comments : './comments.js',
    card: './card.js',
    table : "./table.js",
    semanticTable : './semantic-table.js',
  },
  output: {
    path: 'bundle',
    filename: '[name].js'
  },
  module: {
    loaders:[
      {
        test: /\.js[x]?$/,
        exclude: /node_modules/,
        loader: 'babel-loader?presets[]=es2015&presets[]=react',
      },
    ]
  },
  plugins: [
      new webpack.DllReferencePlugin({
          context: __dirname,
          manifest: require('./manifest.json'),
      }),
      new webpack.optimize.UglifyJsPlugin({
          compress: {
            warnings: false
          }
      })
/*      new CompressionWebpackPlugin({ //gzip 压缩
          asset: '[path].gz[query]',
          algorithm: 'gzip',
          test: new RegExp(
              '\\.(js|css)$'    //压缩 js 与 css
          ),
          threshold: 10240,
          minRatio: 0.8
      })*/

  ]
};
