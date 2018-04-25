/**
 * props: current   当前位置
 *        total     总数
 * @type {*|Function}
 */
const Progress = React.createClass({

    componentDidMount : function () {
        $(this.refs.progress).progress();
    },

    componentDidUpdate : function(prevProps, prevState) {
        $(this.refs.progress).progress('increment');
    },

    render : function () {
        return (
            <div ref="progress" className="ui success progress"
                 data-value={this.props.current+1} data-total={this.props.total}>
                <div className="bar">
                    <div className="progress"></div>
                </div>
            </div>
        )
    }

});

module.exports.Progress = Progress;