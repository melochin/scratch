import {Link} from "react-router";
import * as brochureAction from "../../action/brochureAction"


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

        const description = brochure.description == null ? '无' : brochure.description;

        return (
            <div className="content">
                <div className="center aligned header">
                    <h1>{brochure.name}</h1>
                </div>
                <div className="center aligned description">
                    <p>{description}</p>
                </div>
                <div className="extra content">
                    <div className="center aligned">
                        <Link className="ui teal button" to={`/brochures/${brochure.id}`} state={{brochure : brochure}}>
                            开始学习
                        </Link>
                    </div>
                </div>
            </div>
        )
    },

    renderModify : function (brochure) {

        const handleSave = () => {
            var submitBrochure = brochure;
            submitBrochure.name = this.refs.name.value;
            submitBrochure.description = this.refs.description.value;
            brochureAction.modify(submitBrochure);
        }

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
                        <button className="ui primary mini button" onClick={handleSave}>保存</button>
                        <button className="ui mini button" onClick={() => this.setState({modify : false})}>取消</button>
                    </div>
                </div>
            </div>
        )
    },


    render : function () {
        const {brochure} = this.props;
        const content = this.state.modify ?
            this.renderModify(brochure) :
            this.renderDefault(brochure);

        return (
            <div className="ui raised card " key={brochure.id}>
                <div className="right aligned meta" >
                    <i className="large edit icon" onClick={() => this.setState({modify : !this.state.modify})}></i>
                    <i className="large trash alternate icon" onClick={() => brochureAction.remove(brochure.id)}></i>
                </div>
                {content}
            </div>
        );
    }

})

module.exports.Brochure = Brochure;