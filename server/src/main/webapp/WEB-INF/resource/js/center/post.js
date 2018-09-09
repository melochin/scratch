import * as episodeAction from './episodeAction';
import {FocusButton} from '../anime/focus/button';
import {Comments} from '../anime/comments';

import React from 'react';
import ReactDOM from 'react-dom';
var ReactCSSTransitionGroup = require('react-addons-css-transition-group'); // ES5 with npm

$(document).ready(function () {
    ReactDOM.render(<Posts />, document.getElementById("posts"))
})

var episodes = [{
    id : "1",
    hot : "0",
    anime : {
        animeName : "test",
        id : "1",
        pic : ""
    },
    url : "www.baidu.com",
    number : "1"

}]


const Posts = React.createClass({

    getInitialState : function () {
        return {
            episodes : new Array(),
            page : new Object(),
            end : false,
            loading : false
        }
    },

    componentDidMount : function () {
        window.onscroll = ()  => {
            // scroll to buttom
            if (window.innerHeight + $(document).scrollTop() == document.body.scrollHeight
                && !this.state.end
                && !this.state.loading) {
                this.handlePage(this.state.page.current + 1);

            }
        }

        this.handlePage(1);
    },

    handlePage : function (pageNo) {
        this.setState({loading : true});
        // episodeAction.page(pageNo, (result) => {
        //     // 更新page
        //     this.state.page = result.page;
        //     // 合并episodes
        //     var newEpisodes = this.state.episodes.concat(result.data);
        //     // 重新渲染
        //     this.setState({episodes : newEpisodes,
        //         page : this.state.page,
        //         end : this.statTe.page.current == this.state.page.total,
        //         loading:false});
        // });
        this.setState({episodes : episodes, page : 1, end : true, loading:false});
    },

    refresh : function () {
        episodeAction.pageFrom(1, this.state.page.current, (result) => {

            this.state.page = result.page;
            var newEpisodes = result.data;
            this.setState({episodes : newEpisodes,
                page : this.state.page,
                end : this.state.page.current == this.state.page.total});

        });
    },

    showCommnets : function(reactEle) {
        ReactDOM.render(reactEle, this.refs.content);
        $(this.refs.modal).modal("show");
    },

    render : function () {

        var posts = this.state.episodes.map(e => <Post episode = {e} key = {e.id}
                                                       refresh={this.refresh}
                                                        showCommnets={this.showCommnets}/>);
        return (
            <ReactCSSTransitionGroup
                transitionName="example"
                transitionAppear={true}
                transitionEnterTimeout={500}
                transitionLeaveTimeout={300}>
                <div className="ui one stackable cards">
                    {posts}

                {
                    this.state.loading &&
                    <div className="ui active loader"></div>
                }
                {
                    this.state.end &&
                    <div className="ui card">
                        <div className="content" >
                            已拉到最底
                        </div>
                    </div>
                }
                </div>
                <div className="ui modal" ref={"modal"} >
                    <div className="content" ref={"content"}></div>
                </div>
            </ReactCSSTransitionGroup>
        );
    }

})

/**
 * props : episode
 */
const Post = React.createClass({

   render : function () {

       var id = this.props.episode.id;
       var hot = this.props.episode.hot;
       var animeName = this.props.episode.anime.name;
       var animeId = this.props.episode.anime.id;
       var animePic = this.props.episode.anime.pic;
       var url = this.props.episode.url;
       var number = this.props.episode.number;
       var commments = <Comments animeId={animeId} episodeId={id}/>;
        return (
            <div className="ui card" >
                <div className="content head">
                    <div className="left floated">
                        <a href={'/anime/' + animeId} >{animeName}</a>
                    </div>
                    <FocusButton focus={true} animeId={animeId} onUnfocus={this.props.refresh}/>
                </div>
                <img className="ui image" src={'/upload/' + animePic}></img>
                <div className="content" >
                    <div className="description">
                        <a href={'/episode/' + id + '/redirect?url=' + url} target="_blank">{number}</a>
                    </div>
                </div>
                <div className="extra content">
                    <div>
                        <i className="heart outline large icon"></i>
                        {hot}
                    </div>
                    <div className="comment">
                        <i className="comment outline large icon" onClick={() => this.props.showCommnets(commments)}></i>
                    </div>
                </div>
            </div>
        );
   }

});
