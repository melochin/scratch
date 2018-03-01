$(document).ready(function () {
    ReactDOM.render(<Comments animeId={173}/>, document.getElementById("comments"));
});

var Comments = React.createClass({

    getInitialState : function () {
        return {
            comments : new Array(),
            isLogin : false,
            userId : null
        }
    },

    componentDidMount : function () {
        var _this = this;
        this.list();
        Ajax.get("/api/islogin", null, {
            success : function (data) {
                _this.setState({
                    isLogin : data.isLogin,
                    userId : data.userId
                });
            }
        });
    },


    list : function () {
        var _this = this;
        var url = "/anime/comments/" + this.props.animeId;
        Ajax.get(url, null, {
            success : function (data) {
                _this.setState({comments : data});
            }
        });
    },


    handleComment : function (comment) {
        // 评论之后，要刷新list
        var _this = this;
        var animeComment = new Object();
        animeComment.animeId = this.props.animeId;
        animeComment.comment = comment;
        Ajax.post("/anime/comments", animeComment, {
            success : function(data) {
                _this.list();
            }
        });
    },

    handleDelete : function (comment) {
        var _this = this;
        Ajax.delete("/anime/comments", comment, {
            success : function (data) {
                _this.list();
            }
        })
    },


    render : function () {

        var login = this.state.isLogin;

        return (
            <div className="comments">
                <CommentInput isLogin={login}
                              onComment = {this.handleComment}/>
                <CommentList comments={this.state.comments}
                             userId={this.state.userId}
                            onDelete={this.handleDelete} />
            </div>
        )
    }
   
    
});

var CommentList = React.createClass({

    renderList : function (animeComments) {
        var _this = this;
        return animeComments.map((animeComment, index) => {
/*            var start = (_this.state.page - 1) * 10;
            var end = _this.state.page * 10 - 1;
            if(index < start || index > end) return null;*/
            var date = new Date(animeComment.date).format('yyyy-mm-dd');
            return (
                <div className="event">
                    <div className="label">
                        <img src="https://semantic-ui.com/images/avatar2/small/elyse.png"></img>
                    </div>
                    <div className="content">
                        <div className="summary">
                            <a className="user">
                                {animeComment.username}
                            </a>
                            <div className="date">
                            </div>
                        </div>
                        <div className="extra text">
                            {animeComment.comment}
                        </div>
                        <div className="meta">
                            <h>{date}</h>
                            {animeComment.userId == _this.props.userId &&
                            <a onClick={()=>_this.props.onDelete(animeComment)}>删除</a>
                            }
                        </div>
                    </div>
                </div>
            );
        });
    },


    render : function () {
        if(this.props.comments.length == 0) return null;
        var list = this.renderList(this.props.comments)
        return (
            <div className="ui feed">
                {list}
            </div>
        )
    }
    
});

var CommentInput = React.createClass({

   getDefaultProps : function () {
       return {
           isLogin : true,
       }
   },

   handleClick : function () {
       this.props.onComment($(this.refs.input).val());
       $(this.refs.input).val("");
   },

   renderUnLogin : function () {
       return (
           <div className="ui form">
               <div className="field">
                   <label>评论<a href="/user/login">(请先登录)</a></label>
                   <textarea rows="2" disabled="disabled"></textarea>
               </div>
           </div>
       )
   },

   render : function () {


       var isLogin = this.props.isLogin;

       if(!isLogin) {
           return this.renderUnLogin();
       }
        return (
            <div className="ui form">
                <div className="field">
                    <label>评论</label>
                    <textarea rows="2" ref="input"></textarea>
                </div>
                <button className="ui primary button" onClick={this.handleClick}>提交</button>
            </div>
        )
   }

});