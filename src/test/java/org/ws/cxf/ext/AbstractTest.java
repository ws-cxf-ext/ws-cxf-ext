package org.ws.cxf.ext;

import static org.junit.Assert.fail;

import java.io.File;

/**
 * Classe abstraite pour tout les types de tests JUnit
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public abstract class AbstractTest {
    /**
     * Faire echouer un test en cas d'exception (avec affichage de la stack
     * trace)
     * 
     * @param Exception e
     */
    public void failWithException(Exception e) {
        e.printStackTrace();
        fail("Exception inattendue : " + e.getMessage());
    }

    /**
     * Récupère le chemin du répertoire data relatif au TI en cours ATTENTION :
     * avec un un / à la fin
     * 
     * @return String
     */
    public String getTestDataDir() {
        return "src" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "test" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "resources" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "data" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + this.getClass().getSimpleName() // $NON-NLS-1$
                + File.separator;
    }

    /**
     * Récupère le chemin du répertoire data relatif au TI en cours ATTENTION :
     * avec un un / à la fin
     * 
     * @return String
     */
    public String getTestCommonDataDir() {
        return "src" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "test" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "resources" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "data" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "Common" // $NON-NLS-1$
                + File.separator;
    }

    /**
     * Récupère le chemin du répertoire resources ATTENTION : avec un un / à la
     * fin
     * 
     * @return String
     */
    public String getTestResourcesDir() {
        return "src" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "test" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "resources" // $NON-NLS-1$
                + File.separator;
    }

    /**
     * Récupère le chemin du répertoire data relatif au traitement en cours
     * ATTENTION : avec un un / à la fin
     * 
     * @return String
     */
    public String getDataDir() {
        return "src" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "main" // $NON-NLS-1$
                + File.separator // $NON-NLS-1$
                + "resources" // $NON-NLS-1$
                + File.separator;
    }
}
