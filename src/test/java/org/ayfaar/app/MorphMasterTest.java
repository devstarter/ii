package org.ayfaar.app;

import org.ayfaar.app.utils.MorphMaster;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class MorphMasterTest {
    @Test
    public void test() {
        final Set<String> forms = MorphMaster.getAllForms("депендентными");
        Assert.assertTrue(forms.contains("депендентный"));
        Assert.assertTrue(forms.contains("депендентность"));
        Assert.assertTrue(forms.contains("депендентностью"));
        Assert.assertTrue(forms.contains("депендентности"));
        Assert.assertTrue(forms.contains("депендентно"));
//        Assert.assertTrue(forms.contains("депендентной"));
        Assert.assertTrue(forms.contains("депендентны"));
        Assert.assertTrue(forms.contains("депендентные"));
        Assert.assertTrue(forms.contains("депендентных"));
        Assert.assertTrue(forms.contains("депендентным"));
        Assert.assertTrue(forms.contains("депендентными"));
        Assert.assertTrue(forms.contains("депендентного"));
        Assert.assertTrue(forms.contains("депендентном"));
    }
}
