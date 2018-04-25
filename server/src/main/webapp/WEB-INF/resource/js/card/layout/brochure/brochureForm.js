
/**
 * 册子——新增表单
 *
 * props: onSubmit(brouchure)   表单提交的处理
 *        onCancel()            表单取消的处理
 *
 */
var BrochureForm = React.createClass({

    handleSubmit : function() {
        var brouchure = new Object();
        brouchure.name = this.refs.name.value ;
        brouchure.description = this.refs.description.value;
        this.props.onSubmit(brouchure);
    },

    render : function () {
        return (
            <div id="brochure-form" className="ui form" >
                <div className="two fields">
                    <div className="field">
                        <label>名字</label>
                        <input ref="name"/>
                    </div>
                    <div className="field">
                        <label>描述</label>
                        <input ref="description"/>
                    </div>
                </div>
                <button className="ui primary button" onClick={this.handleSubmit}>提交</button>
                <button className="ui button" onClick={this.props.onCancel}>取消</button>
            </div>
        )
    }
});

module.exports.BrochureForm = BrochureForm;
