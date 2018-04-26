import {Card} from './card'

const CardList = React.createClass({

    render : function() {
        var cards = this.props.contents.map(
            (content, index) => <Card card={content} no={index} key={content.id}
                                      onDelete={this.props.onDelete}
                                      onSwap={this.props.onSwap}/> );
        return (
            <table className="ui selectable fixed table">
                <thead>
                    <tr>
                        <th className="two wide">序号</th>
                        <th className="six wide">正面</th>
                        <th className="six wide">反面</th>
                        <th className="two wide"></th>
                    </tr>
                </thead>
                <tbody>
                    {cards}
                </tbody>
            </table>
        )
    },
})

module.exports.CardList = CardList;