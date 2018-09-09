import dispatcher from '../layout/brochure/dispatcher';

function list() {
    Ajax.get("/api/brochures", null, {
        success : (brochures) => {
            console.log("dispatch action");
            dispatcher.dispatch({type : "list", brochures})
        }
    });
}

function save(brochure) {
    Ajax.post("/api/brochures", brochure, {
          success : (brochure) => {
            message("新增成功");
            dispatcher.dispatch({type : "save", brochure});
        }
    });
}

function modify(sendBrochure) {
    Ajax.put("/api/brochures", sendBrochure, {
        success : (brochure) => {
            message("修改成功");
            dispatcher.dispatch({type: "modify", brochure})
        }
    });
}

function remove(brochureId) {
    Ajax.delete("/api/brochures/" + brochureId, null, {
        success : (brochure) => {
            console.log(brochure);
            if(brochure == null || brochure == '') {
                message("删除失败");
            } else {
                message("删除成功")
                dispatcher.dispatch({type : "delete", brochure});
            }
        }
    });
}

export {save, list, modify, remove}

