var webpack = require("webpack");
var CompressionWebpackPlugin = require('compression-webpack-plugin');
var UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = {
  entry: {
    run : './run.js',
    card: './card.js',
    table : "./table.js",
    semanticTable : './semantic-table.js',
    post : './center/post.js',
    common : './common/common.js'
  },
  output: {
    path: __dirname + '/bundle',
    filename: '[name].js'
  },
module: {
    rules: [
          {
            test: /\.js[x]?$/,
            exclude: /node_modules/,
            use: {
                loader: 'babel-loader',
                options: {
                    presets: ['babel-preset-es2015', 'babel-preset-react']
                }
            }
          }
    ]
    },
  plugins: [
      new webpack.DllReferencePlugin({
          context: __dirname,
          manifest: require('./manifest.json'),
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

  ],
   // optimization: {
   //      minimizer: [
   //          new UglifyJsPlugin({
   //              uglifyOptions: {
   //                  compress: false
   //                  }
   //              })
   //      ]
   //  }
};
