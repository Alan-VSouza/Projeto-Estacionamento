package br.ifsp.demo.suits;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("br.ifsp.demo.integration") // ajuste para o pacote-base dos testes
@IncludeTags("IntegrationTest")
public class Integration {
}
