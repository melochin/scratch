import {CardLayout} from "../card/layout";

/**
 * props : brochure
 *         delete(brochure)
 * @type {*|Function}
 */
var Brochure = React.createClass({

    getInitialState : function () {
        return {
            modify : false,
        }
    },

    componentWillReceiveProps : function (nextProps) {
        this.setState({modify : false})
    },

    renderDefault : function (brochure) {
        return (
            <div className="content">
                <div className="center aligned header">
                    <h1>{brochure.name}</h1>
                </div>
                <div className="center aligned description">
                    <p>{brochure.description == null ? '无' : brochure.description}</p>
                </div>
                <div className="extra content">
                    <div className="center aligned">
                        <button className="ui teal button" onClick={() =>
                            ReactDOM.render(<CardLayout brochure={brochure}/>, document.getElementById("container"))}>开始学习</button>
                    </div>
                </div>
            </div>
        )
    },

    renderModify : function (brochure) {
        return (
            <div className="content ui form">
                <div className="center aligned header">
                    <input ref="name"></input>
                </div>
                <div className="center aligned description">
                    <input ref="description"></input>
                </div>
                <div className="extra content">
                    <div className="center aligned">
                        <button className="ui primary mini button" onClick={() => {
                            var submitBrochure = this.props.brochure;
                            submitBrochure.name = this.refs.name.value;
                            submitBrochure.description = this.refs.description.value;
                            this.props.modify(submitBrochure);
                        }}>保存</button>
                        <button className="ui mini button" onClick={() => this.setState({modify : false})}>取消</button>
                    </div>
                </div>
            </div>
        )
    },


    render : function () {
        var brochure = this.props.brochure;
        var content = this.state.modify ? this.renderModify(brochure) : this.renderDefault(brochure);
        return (
            <div className="ui raised card " key={brochure.id}>
                <div className="right aligned meta" >
                    <i className="large edit icon" onClick={() => this.setState({modify : !this.state.modify})}></i>
                    <i className="large trash alternate icon" onClick={() => this.props.delete(brochure)}></i>
                </div>
                {content}
            </div>
        );
    }

})

module.exports.Brochure = Brochure;