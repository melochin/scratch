var Remarkable = require('remarkable');

// This values are default
var md = new Remarkable({
    html:         false,        // Enable html tags in source
    xhtmlOut:     false,        // Use '/' to close single tags (<br />)
    breaks:       false,        // Convert '\n' in paragraphs into <br>
    langPrefix:   'language-',  // CSS language prefix for fenced blocks
    linkify:      false,        // Autoconvert url-like texts to links
    typographer:  false,        // Enable smartypants and other sweet transforms
    // Highlighter function. Should return escaped html,
    // or '' if input not changed
    highlight: function (/*str, , lang*/) { return ''; }
});

var Card = React.createClass({

    getInitialState : function() {
        return {front : true}
    },

    componentDidMount : function () {
        var _this = this;
        document.addEventListener("keydown", function(event) {
            if(event.code == "ArrowDown") {
                $(_this.refs.btn).find("button").click();
            }
        });

    },

    // 每次接受到新属性，初始化为正面
    componentWillReceiveProps : function() {
        this.setState({front : true});
    },

    getContent : function () {
        if(this.state.front) return this.props.content['key'];
        return this.props.content['value'];
    },

    handleClick : function() {
        this.setState({front : !this.state.front});
    },

    renderButton : function () {
        var btn = null;
        // 如果是正面　button：翻转卡片
        if(this.state.front) {
            btn = <button className="ui fluid button" onClick={this.handleClick}>翻转卡片</button>
            return btn;
        }
        // 如果是反面且不是最后一张卡　button:下一张卡片
        if(!this.props.isLast) {
            btn = <button className="ui fluid button" onClick={this.props.onRightClick}>下一张卡片</button>
            return btn;
        }

        // 反面且最后一张　button:完成
        btn  = <button className="ui teal fluid button" onClick={() => {
                this.props.onRightClick();
                this.props.onComplete();}}>完成</button>
        return btn;
    },

    render : function() {
        var button = this.renderButton();
        return(
            <div className="ui centered card" style={{width:"100%"}}>
                <div className="description center aligned twelve wide column" style={{fontSize: "2rem", padding: "6rem 0rem"}} onClick={this.handleClick} >
                    <div dangerouslySetInnerHTML={{__html:md.render(this.getContent())}}/>
                </div>
                <div className="extra content">
                    <div className="ui grid">
                        <div className="ui five wide computer only column"></div>
                        <div className="ui sixteen wide tablet six wide computer column " ref="btn">
                            {button}
                        </div>
                        <div className="ui five wide computer only column"></div>
                    </div>


                </div>
            </div>
        )
    }
});

/**
 * props: current   当前位置
 *        total     总数
 * @type {*|Function}
 */
var Progress = React.createClass({

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

var Cards = React.createClass({
    getInitialState : function() {
        return ({
            current : 0
        })
    },

    handleClick : function() {
        if(this.state.current < this.props.contents.length - 1) {
            this.setState({current : this.state.current + 1});
        }
    },

    setCurrent : function(current) {
        if(current < 0 || current >= this.props.contents.length) {
            return;
        }
        this.setState({current : current});
    },

    renderCard : function(index) {
        var _this = this;
        if(index >= this.props.contents.length || index < 0) return null;
        var content = this.props.contents[index];
        var isFirst = false;
        var isLast = false;
        if(index == 0) {
            isFirst = true;
        }
        if(index == this.props.contents.length - 1) {
            isLast = true;
        }

        return <Card content={content} isFirst = {isFirst} isLast = {isLast}
                     onComplete = {this.props.onComplete}
                     onLeftClick={() => (this.setCurrent(this.state.current - 1))}
                     onRightClick={()=> {
                         Ajax.put("/api/brochures/" + _this.props.brochure.id + "/cards/memory", content, {
                             success : function () {
                                 _this.setCurrent(_this.state.current + 1)
                             }
                         });
                     }}/>
    },

    render : function() {
        return (
            <div className="ui container" style={{margin : "2rem"}}>
                {this.renderCard(this.state.current)}
                <Progress current={this.state.current} total={this.props.contents.length} />
            </div>
        )
    }
})

module.exports.Cards = Cards;