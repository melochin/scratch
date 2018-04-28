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

function modify(brochure, callback) {
    Ajax.put("/api/brochures", brochure, {
        success : () => {
            message("修改成功");
            list();
        }
    });
}

function remove(brochureId) {
    Ajax.delete("/api/brochures/" + brochureId, null, {
        success : (brochure) => {
            message("删除成功")
            dispatcher.dispatch({type : "delete", brochure});
        }
    });
}

export {save, list, modify, remove}

