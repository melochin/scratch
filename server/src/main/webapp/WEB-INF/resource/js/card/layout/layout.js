import {Brochures} from "./brochure/brochures";

const containerId = "container";

const Menu = React.createClass({

    render : function () {
        return (
            <div className="ui container" style={{padding: "0px 20px"}}>
                <div className="ui text menu">
                    <div className="active item"
                         onClick={() => ReactDOM.render(<Brochures />, document.getElementById(containerId))}>
                        首页
                    </div>
                </div>
            </div>
        )
    }

})

const Container = React.createClass({
    render : function () {
        return (
            <div id={containerId}>
                <Brochures />
            </div>
        )
    }
})

const Layout = React.createClass({

    render : function () {
        return (
            <div>
                <Menu/>
                <Container/>
            </div>
        );
    }
});

module.exports.Layout = Layout;