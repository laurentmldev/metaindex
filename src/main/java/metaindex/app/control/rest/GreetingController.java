package metaindex.app.control.rest;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/*
import org.apache.struts2.rest.HttpHeaders;
import org.apache.struts2.rest.DefaultHttpHeaders;

import com.opensymphony.xwork2.ModelDriven;

import metaindex.app.control.rest.messages.Greeting;

import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.convention.annotation.Result;

@Results({
    @Result(name="success", type="redirectAction", params = {"actionName" , "greetings"})
})
public class GreetingController implements ModelDriven<Object> {

    private Greeting model = new Greeting("abcd","Hello sweet admin");

    // Handles /orders/{id} GET requests
    public HttpHeaders show() {
        //model = new Greeting("abcd","Hello sweet admin");
        return new DefaultHttpHeaders("show");        			
            //.withETag(model.getUniqueStamp())
            //.lastModified(model.getLastModified());
    }

    public HttpHeaders index() {
        return new DefaultHttpHeaders("index")
            .disableCaching();
    }
    
    // Handles /orders/{id} PUT requests
    public String update() {
        return "update";
    }
    
    // GET /orders/1/edit
    public String edit() {
        return "edit";
    }

    // GET /orders/new
    public String editNew() {
        return "editNew";
    }

    // GET /orders/1/deleteConfirm
    public String deleteConfirm() {
        return "deleteConfirm";
    }

    // DELETE /orders/1
    public String destroy() {
        //addActionMessage("Order removed successfully");
        return "success";
    }

    // POST /orders
    public HttpHeaders create() {
        //addActionMessage("New order created successfully");
        return new DefaultHttpHeaders("success")
            .setLocationId(model.getId());
    }

    public void setId(String id) {
       
    }
	@Override
	public Object getModel() {
		return model;
	}
		
}
*/