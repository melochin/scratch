var ClipboardJS = require('clipboard');
var md = require('../../../markdown').markdown;

/**
 * props : no
 *         card
 *         onDelete
 * @type {*|Function}
 */
const Card = React.createClass({

    componentDidMount : function () {
        var _this = this;
        new ClipboardJS(this.refs.key, {
            text : function (trigger) {
                return _this.props.card.key
            }
        });

        new ClipboardJS(this.refs.value, {
            text : function (trigger) {
                return _this.props.card.value
            }
        });

    },

    handleDrop : function (event) {
        var id = event.dataTransfer.getData("Text");
        if(id == null) return;
        console.log(id);
        //console.log(this.props.card); ???怎么可以放到　目标元素的props?
        this.props.onSwap(this.props.card.id, id);
    },

    render : function () {
        return (
            <tr className="item" ref="card" key={this.props.card.id}
                 draggable={true} onDragOver={event => event.preventDefault()}
                 onDragStart={(event) => event.dataTransfer.setData("Text", this.props.card.id)}
                 onDragEnter={(event) => this.refs.card.style.background = "#bbeaf3" }
                 onDragLeave={(event) => this.refs.card.style.background = ""}
                 onDrop={(event) => {
                     this.handleDrop(event)
                     this.refs.card.style.background = ""
                 }}>
                <td className="disabled">
                    {this.props.no}
                </td>
                <td>
                    <span dangerouslySetInnerHTML={{__html:md.render(this.props.card.key)}} ref="key"/>
                </td>
                <td>
                    <span dangerouslySetInnerHTML={{__html:md.render(this.props.card.value)}} ref="value"/>
                </td>
                <td>
                    <button className="ui right floated icon button"
                            style={{background : "white"}} onClick={() => this.props.onDelete(this.props.card, null)}>
                        <i className="trash outline icon"></i>
                    </button>
                </td>
            </tr>
        )
    }
})

module.exports.Card = Card;