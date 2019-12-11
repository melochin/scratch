$(document).ready(() => {
    Post.render();

    $(window).scroll(() => {
        if (document.body.clientHeight + document.body.scrollTop >= document.body.scrollHeight) {
            Post.load();
        }
    })
});

const Post = {

    currentPage: 1,

    maxPage: 1,

    unFocus: (animeId) => {
        Action.unFocus(animeId, (res) => {
            Post.refresh();
        })
    },

    renderVote: (episodeId, votes, vote) => {
        const heartStyle = vote ? "red" : "outline";
        return `
            <div>
                 <i class="${heartStyle} heart large icon"
                    onclick="Post.vote(${!vote}, ${episodeId})">${votes}</i>
            </div>
        `
    },

    renderSingle: (episode) => {
        const anime = episode.anime;

        const voteCom = Post.renderVote(episode.id, episode.votes, episode.vote);
            return `
            <div class="ui card" >
                <div class="content ">
                    <a href=${'/anime/' + anime.id}>${anime.name}</a>
                    <div class="right floated">
                        <div class="ui dropdown">
                          <i class="large dropdown icon"></i>
                          <div class="menu">
                            <div class="item" onclick="Post.unFocus(${anime.id})">取消关注</div>
                            <div class="item">链接失效</div>
                          </div>
                        </div>
                    </div>
                </div>
                    <div class="image">
                    <img src="/pic/${anime.pic}">
                </div>
                <div class="content" >
                    <div class="description">
                        <a href=${'/episode/' + episode.id + '/redirect?url=' + episode.url}
                            target="_blank">${episode.number}</a>
                    </div>
                </div>
                <div class="extra content">
                    ${voteCom}
                    <div class="comment">
                        <i class="comment outline large icon"
                            onclick="Comment.showModal(${anime.id}, ${episode.id})"></i>
                    </div>
                </div>
            </div>
    `;

    },

    vote: (vote, episodeId) => {
        var _event = event;

        if (vote == false) {
            Action.cancelVote(episodeId, (votes) => {
                if (votes == -1) return;
                $(_event.target).replaceWith(Post.renderVote(episodeId, votes, false));
            })
        } else {
            Action.vote(episodeId, (votes) => {
                if (votes == -1) return;
                $(_event.target).replaceWith(Post.renderVote(episodeId, votes, true));
            })
        }
    },

    refresh: () => {
        Action.page(1, Post.currentPage, (res) => {
            const eles = res.data.map(d => Post.renderSingle(d));
            $("#posts").html(eles);
            $('.ui.dropdown').dropdown();
            Post.currentPage = res.page.current;
            Post.maxPage = res.page.total;
        });
    },

    load: () => {
        console.log(Post.currentPage, Post.maxPage);
        if(Post.currentPage == Post.maxPage) return;

        Action.page(Post.currentPage + 1, null, (res) => {
            const eles = res.data.map(d => Post.renderSingle(d));
            $("#posts").append(eles);
            $('.ui.dropdown').dropdown();
            Post.currentPage = res.page.current;
            Post.maxPage = res.page.total;
        });
    },

    render: () => {
        Action.page(Post.currentPage, null, (res) => {
            const eles = res.data.map(d => Post.renderSingle(d));
            $("#posts").append(eles);
            $('.ui.dropdown').dropdown();

            Post.maxPage = res.page.total;
        });
    }

}


const Action = {

    page: (start, end, callback) => {
        var url = "";

        if (end == null) {
            url = `/api/user/episodes/page/${start}`
        } else {
            url = `/api/user/episodes/page/${start}/${end}`
        }

        Ajax.get(url, null, {
            success: function (data) {
                callback(data);
            }
        })

    },



    comments: (animeId, episodeId, callback) => {
        Ajax.get(`/api/anime/comments/${animeId}/${episodeId}`, null, {
            success: function (data) {
                callback(data);
            }
        })
    },

    postComment: (comment, callback) => {
        Ajax.post("/api//anime/comments", comment, {
            success: function (data) {
                callback(data);
            }
        })
    },

    unFocus: (animeId, callback) => {
        Ajax.delete(`/api/user/animes/${animeId}/focus`, null, {
            success: function(data) {
                callback(data);
            }
        })
    },

    vote: (episodeId, callback) => {
        Ajax.post(`/api/votes/${episodeId}`, null, {
            success: function(data) {
                callback(data.votes);
            }
        })
    },

    cancelVote: (episodeId, callback) => {
        Ajax.delete(`/api/votes/${episodeId}`, null, {
            success: function(data) {
                callback(data.votes);
            }
        })
    }

}


const Comment = {
    animeId: null,
    episodeId: null,

    listComments: () => {
        Action.comments(this.animeId, this.episodeId, (data) => {
            const elements = data.map(c => renderComment(c));
            $("#comments").html(elements);
        })
    },

    showModal: (animeId, episodeId) => {

        console.log(this);

        this.animeId = animeId;
        this.episodeId = episodeId;

        $('.ui.modal')
            .modal('show')
        ;

        Comment.listComments();
    },

    postComment: () => {
        const obj = {
            animeId: this.animeId,
            episodeId: this.episodeId,
            comment: $("#input-comment").val()
        }
        Action.postComment(obj, (data) => {
            $("#input-comment").val("");
            Comment.listComments();
        });
    }

}


function renderComment(coment) {
    return `
    <div class="event">
        <div class="label">
            <img src="https://semantic-ui.com/images/avatar2/small/elyse.png"></img>
        </div>
        <div class="content">
            <div class="summary">
                <a class="user">
                    ${coment.username}
                </a>
                <div class="date">
                </div>
            </div>
            <div class="extra text">
                ${coment.comment}
            </div>
            <div class="meta">
            </div>
        </div>
    </div>
    `
}
