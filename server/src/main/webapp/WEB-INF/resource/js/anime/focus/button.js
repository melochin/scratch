var React =  require('react');

/**
 * props:
 * focus true/false 关注状态
 * animeId
 * onFocus callback
 * onUnfocus callback
 */
var FocusButton = React.createClass({

    getDefaultProps : function () {
        return {
            onFocus : function () {},
            onUnfocus: function () {}
        }
    },

    getInitialState : function() {
        return {
            focus : this.props.focus
        }
    },

    focus : function() {
        var _this = this;
        if(_this.state.focus) {
            // 取消关注
            $.ajax("/api/user/animes/" + this.props.animeId + "/unfocus",{
                success : function() {
                    _this.setState({focus : false});
                    _this.props.onUnfocus();
                }
            })
        } else {
            // 关注
            $.ajax("/api/user/animes/" + this.props.animeId + "/focus",{
                success : function() {
                    _this.setState({focus : true});
                    _this.props.onFocus();
                }
            })
        }
    },

    render : function() {

        var text =  this.state.focus ? "取消关注" : "关注";
        var className = this.state.focus ? "ui basic mini button" : "ui red basic mini button";

        return(
            <button className={className} onClick={this.focus}>
                {text}
            </button>
        );
    }

});

module.exports.FocusButton = FocusButton;
