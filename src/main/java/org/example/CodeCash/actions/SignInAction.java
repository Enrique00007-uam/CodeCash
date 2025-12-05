package org.example.CodeCash.actions;


import com.openxava.naviox.actions.ForwardToOriginalURIBaseAction;
import com.openxava.naviox.impl.SignInHelper;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;
import javax.persistence.Query;

public class SignInAction extends ForwardToOriginalURIBaseAction {

    public void execute() throws Exception {
        SignInHelper.initRequest(getRequest(), getView());
        if (getErrors().contains()) return;
        String userName = getView().getValueString("user");
        String password = getView().getValueString("password");
        if (Is.emptyString(userName, password)) {
            addError("Usuario o contraseña vacios");
            return;
        }
        Long count = 0L;
        Query query = XPersistence.getManager().createQuery("select count(e) from AppUsuario e where  e.usuario = :username and e.password = :password");
        query.setParameter("username", userName);
        query.setParameter("password", password);
        count = (Long)query.getSingleResult();
        if (count == 0L) {
            addError("unauthorized_user");
            return;
        }
        /*if (!SignInHelper.isAuthorized(getRequest(), userName, password, getErrors())) {
            return;
        }*/
        SignInHelper.signIn(getRequest(), userName);
        getView().reset();
        getContext().resetAllModulesExceptCurrent(getRequest());
        forwardToOriginalURI();
    }

}