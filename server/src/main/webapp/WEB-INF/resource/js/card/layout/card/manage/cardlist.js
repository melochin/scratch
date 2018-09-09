import {Card} from './card'
import * as cardAction from '../../../action/cardAction'

const CardList = React.createClass({

    getInitialState : function () {
        return {contents : this.props.contents};
    },

    componentDidMount : function () {
        const word = this.props.params.word;
        cardAction.listByWord(word, (data) => this.setState({contents: data}));
    },

    componentWillReceiveProps : function (nextProps) {
        this.setState({contents : nextProps.contents})
    },


    render : function() {
        const cards = this.state.contents == null ? null :
            this.state.contents.map((content, index) => <Card card={content} no={index} key={content.id}
                                      onDelete={this.props.onDelete}
                                      onSwap={this.props.onSwap}/> );
        return (
            <table className="ui selectable fixed table">
                <thead>
                    <tr>
                        <th className="two wide">序号</th>
                        <th className="five wide">正面</th>
                        <th className="five wide">反面</th>
                        <th className="two wide">记忆信息</th>
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