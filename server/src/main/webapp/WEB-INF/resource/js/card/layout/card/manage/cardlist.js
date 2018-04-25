import {Card} from './card'

const CardList = React.createClass({

    render : function() {
        var cards = this.props.contents.map(
            (content, index) => <Card card={content} no={index} key={content.id}
                                      onDelete={this.props.onDelete}
                                      onSwap={this.props.onSwap}/> );
        return (
            <div className="ui divided items">
                {cards}
            </div>
        )
    },
})

module.exports.CardList = CardList;