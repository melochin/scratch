var React =  require('react');
var ReactDOM = require('react-dom');
import { Icon, Label, Menu, Table, Checkbox, Modal } from 'semantic-ui-react'

$(document).ready(function () {
   ReactDOM.render(<TempList/>, document.getElementById("container"));
});


var Action = {
    list : function(callback) {
        Ajax.get("/api/admin/scratch/episodes?status=0", null, {
            success : (data) => callback(data)
        })
    },

    shouldCover : function(animeId, hostId, number, callback) {
        Ajax.get("/api/admin/episodes?animeId=" + animeId
            + "&hostId=" + hostId
            + "&no=" + number, null, {
                success : (data) => callback(data)
            })
    },

    pass : function(id, callback) {
      Ajax.post("/api/admin/scratch/episodes/pass=" + id, null , {
          success : (data) => callback(data)
      });
    }
}


var TempList = React.createClass({

    getInitialState : function () {
        var _this = this;
        _this.chooseMap = new Map();
        Ajax.get("/api/dics/01", null, {
            async : false,
            success : function (data) {
                _this.hostMap = data;
            }
        });
        return {
            data : new Array(),
            openModal : false,
            episode : null,
            scratch : null,
        }
    },

    componentDidMount : function () {
        this.list();
    },

    list : function () {
        Action.list((data) => this.setState({data: data}));
    },
    //
    // tryPass : function (data) {
    //     var _this = this;
    //     Ajax.get("/api/admin/episodes?animeId=" + data.anime.id
    //         + "&hostId=" + data.hostId
    //         + "&no=" + data.number , null, {
    //         success : function (result) {
    //             if(result != null && result.length > 0 && result[0] != null) {
    //                 _this.setState({openModal : true, episode : result[0], scratch : data});
    //             } else {
    //                 _this.handlePass(data.id);
    //             }
    //         }
    //     })
    // },
    //
    // handleCover : function (scratchId, episodeId) {
    //     var _this = this;
    //     Ajax.post("/api/admin/scratch/episodes/cover/" + scratchId
    //         + "/" + episodeId, null, {
    //         success : function () {
    //             _this.setState({openModal : false});
    //             _this.list();
    //         }
    //     });
    // },

    handlePass : function (data) {
        if(true) {
            this.setState({openModal : true, scratch : data});
        }
    },

    handleRejct : function (id) {
        var _this = this;
        Ajax.post("/api/admin/scratch/episodes/reject=" + id, null , {
            success : function () {
                _this.list();
            }
        });
    },

    render : function () {
        return (
            <div>
                <TempTable data={this.state.data} hostMap={this.hostMap} handlePass={this.handlePass} handleRejct={this.handleRejct}/>
                <MessageModal open={this.state.openModal}
                            onClick={(id, number) => {
                                Action.pass(id, (data) => this.list());
                                this.setState({openModal : false}) } }
                              onClose={() => (this.setState({openModal : false}))}
                              episode={this.state.episode} scratch={this.state.scratch} />
            </div>
            )
    }

});


var TempTable = React.createClass({

    shouldComponentUpdate : function (nextProps, nextState) {
        console.log(nextProps, this.props);
        if(nextProps.data == this.props.data) {
            return false;
        }
        return true;
    },

    renderHeader : function () {
        return (
            <Table.Row>
                <Table.HeaderCell singleLine>名字</Table.HeaderCell>
                <Table.HeaderCell width={5}>集</Table.HeaderCell>
                <Table.HeaderCell>来源</Table.HeaderCell>
                <Table.HeaderCell>地址</Table.HeaderCell>
                <Table.HeaderCell width={3}>操作</Table.HeaderCell>
            </Table.Row>
        );
    },

    renderBodyRow : function (data) {
        return (
            <Table.Row key={data.id}>
                <Table.Cell singleLine>{data.anime.name}</Table.Cell>
                <Table.Cell>{data.number}</Table.Cell>
                <Table.Cell>{this.props.hostMap[data.hostId]}</Table.Cell>
                <Table.Cell><a href={data.url} target="_blank">{data.url}</a></Table.Cell>
                <Table.Cell>
                    <div className="ui mini buttons">
                        <button className="ui teal button" onClick={() => this.props.handlePass(data)}>通过</button>
                        <button className="ui red button" onClick={() => this.props.handleRejct(data.id)}>不通过</button>
                    </div>
                </Table.Cell>
            </Table.Row>
        )
    },

    render : function () {
        var headerRow = this.renderHeader();
        return(
            <Table celled padded tableData={this.props.data}
                   renderBodyRow={(data) => this.renderBodyRow(data)}
                   headerRow = {headerRow}>
            </Table>
        )
    }
});



var MessageModal = React.createClass({

    handleClick : function() {
        var number = this.refs.number.value;
        var id = this.props.scratch.id;
        this.props.onClick(id, number);
    },

    render : function () {

        var scratch = this.props.scratch;
        var name = scratch != null ? scratch.anime.name : "";
        var number = scratch != null ? scratch.number : "";
        var url = scratch != null ? scratch.url : "";
        return (
            <Modal size={'small'} open={this.props.open}
                   onClose={() => (this.props.onClose())}>
                <Modal.Header>
                    {name}
                </Modal.Header>
                <Modal.Content>
                    <form className="ui form">
                        <div className="field">
                            <label>集名</label>
                            <input type="text" value={number} ref="number"/>
                        </div>
                        <div className="field">
                            <label>地址</label>
                            <input type="text" value={url} disabled="disabled"/>
                        </div>
                    </form>
                </Modal.Content>
                <Modal.Actions>
                    <button className="ui teal button" onClick={this.handleClick}>确定</button>
                    <button className="ui basic button" onClick={this.props.onClose}>取消</button>
                </Modal.Actions>
            </Modal>
        )

    }
})
