import * as brochureAction from "../../action/brochureAction"
import brochureStore from "./brochureStore"
/**
 * 册子——新增表单
 *
 * props:
 *        onSubmit(brouchure, callback)   表单提交的处理
 *
 */
const BrochureModal = React.createClass({

    handleSubmit : function() {
        var brouchure = new Object();
        brouchure.name = this.refs.name.value ;
        brouchure.description = this.refs.description.value;
        brochureAction.save(brouchure);
    },

    componentWillMount : function () {
        brochureStore.on("change", () => {
            $(this.refs.modal).modal('hide');
            this.refs.name.value = "";
            this.refs.description.value = "";
        });
    },

    renderModal : function () {
        return (
            <div className="ui small modal" ref="modal">
                    <div id="brochure-form" className="ui form" >
                        <div className="field">
                            <label>名字</label>
                            <input ref="name"/>
                        </div>
                        <div className="field">
                            <label>描述</label>
                            <input ref="description"/>
                        </div>
                        <button className="ui primary button" onClick={this.handleSubmit}>提交</button>
                        <button className="ui button" onClick={() => $(this.refs.modal).modal('hide')}>取消</button>
                    </div>
            </div>
        )
    },

    render : function () {
        return (
            <div className="ui six wide tablet two wide computer column">
                <button className="ui primary button"
                        onClick={() => $(this.refs.modal).modal('show')}>新增</button>
                {this.renderModal()}
            </div>
        )
    }
});

module.exports.BrochureModal = BrochureModal;
