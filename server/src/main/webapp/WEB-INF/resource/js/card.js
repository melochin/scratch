import {Layout} from "./card/layout/layout";
import {Router, Route, IndexRoute, hashHistory} from "react-router";
import {Brochures} from "./card/layout/brochure/brochures";
import {CardLayout} from "./card/layout/card/layout";

$(document).ready(function() {
  ReactDOM.render(
      <Router>
        <Route path="/" component={Layout}>
          <IndexRoute component={Brochures}> </IndexRoute>
          <Route path="brochures/:brochureId" component={CardLayout}/>
        </Route>
      </Router>,
      document.getElementById("card"));
})
