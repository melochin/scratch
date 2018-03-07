$(document).ready(function() {
    $(".anime-focus").map((index) => {
        var element = $(".anime-focus").get(index);
        var focus = $(element).attr("focus");
        if(focus == "true") {
            focus = true;
        } else {
            focus = false;
        }
        var animeId = $(element).attr("anime");
        ReactDOM.render(<Focus focus={focus} animeId={animeId} />, element);
    })
});

var Focus = React.createClass({

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
                }
            })
        } else {
            // 关注
            $.ajax("/api/user/animes/" + this.props.animeId + "/focus",{
                success : function() {
                    _this.setState({focus : true});
                }
            })
        }
    },

    render : function() {
        var text = "关注";
        var className = "empty heart icon";
        console.log(this.state.focus);
        if(this.state.focus) {
            text = "取消关注";
            className = "red heart icon";
        }

        return(
            <a onClick={this.focus}>
                <i className={className}></i>
                {text}
            </a>
        );
    }

});
