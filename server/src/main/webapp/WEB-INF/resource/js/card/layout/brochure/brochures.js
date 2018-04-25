import {Brochure} from './brochure'
import {BrochureForm} from './brochureForm'
import * as brochureAction from '../../action/brochureAction'

var Brochures = React.createClass({

    getInitialState : function () {
        return {
            brochures : new Array(),
            searchOptions : new Array(),
            showForm : false
        }
    },

    componentDidMount : function () {
        this.list();
    },

    list : function () {
        var _this = this;
        brochureAction.list((data) => {
            _this.setState({brochures: data.brochures, searchOptions : data.options});
        });
    },

    delete : function (brochure) {
        var _this = this;
        brochureAction.remove(brochure, () => _this.list());
    },

    save : function (brochure) {
        var _this = this;
        brochureAction.remove(brochure, () => {
            _this.setState({showForm : false});
            _this.list();
            message("新增成功");
        })
    },

    modify : function (brochure, success) {
        var _this = this;
        Ajax.put("/api/brochures", brochure, {
            success : function () {
                _this.list();
            }
        })
    },

    renderBrouchure : function () {
        var _this = this;
        return this.state.brochures.map(brochure => {
            return(
                <Brochure key={brochure.id} brochure={brochure} delete={_this.delete} modify={_this.modify}/>
            )
        });
    },

    render : function () {
        return (
            <div className="ui container" id="card-box" >
                <div className="ui grid">
                    {/*                  <div className="ui three wide column">
                      <Dropdown search fluid selection options={this.state.searchOptions} />
                  </div>*/}
                    <div className="ui six wide tablet two wide computer column">
                        <button className="ui primary button"
                                onClick={() => !this.state.showForm && this.setState({showForm : true})}>新增</button>
                    </div>
                    { this.state.showForm &&
                    <div className="ui five wide column">
                        <BrochureForm onSubmit={this.save}
                                      onCancel={() => this.state.showForm && this.setState({showForm : false})}/>
                    </div>
                    }
                </div>
                <div className="ui five stackable cards">
                    {this.renderBrouchure()}
                </div>
            </div>
        )
    }

});

module.exports.Brochures = Brochures;
