import {Layout} from "./card/layout/layout";
import {Router, Route, IndexRoute, hashHistory} from "react-router";
import {Brochures} from "./card/layout/brochure/brochures";
import {CardLayout} from "./card/layout/card/layout";
import {Print} from "./card/layout/card/manage/print";

$(document).ready(function() {
  ReactDOM.render(
      <Router history={hashHistory}>
        <Route path="/" component={Layout}>
          <IndexRoute component={Brochures}> </IndexRoute>
          <Route path="brochures/:brochureId" component={CardLayout}/>
        </Route>
        <Route path="/print/:brochureId" component={Print}/>
      </Router>,
      document.getElementById("card"));
})
