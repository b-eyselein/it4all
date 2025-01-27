export function BoolCreateInstructions(): JSX.Element {
  return (
    <>
      <hr/>

      <h3 className="subtitle is-4 has-text-info has-text-centered">Folgende Operatoren sind definiert und dürfen verwendet
        werden:</h3>

      <table className="table is-fullwidth">
        <thead>
          <tr>
            <th>Werte</th>
            <th/>
            <th>Darstellung</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Wahr</td>
            <td/>
            <td><code>true</code> oder <code>1</code></td>
          </tr>
          <tr>
            <td>Falsch</td>
            <td/>
            <td><code>false</code> oder <code>0</code></td>
          </tr>
        </tbody>
        <thead>
          <tr>
            <th>Standardoperatoren</th>
            <th>Mathematisches Symbol</th>
            <th>Darstellung</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Nicht</td>
            <td className="has-background-grey-light">&not; a</td>
            <td><code>not a</code></td>
          </tr>
          <tr>
            <td>Und</td>
            <td className="has-background-grey-light">a &and; b</td>
            <td><code>a and b</code></td>
          </tr>
          <tr>
            <td>Oder</td>
            <td className="has-background-grey-light">a &or; b</td>
            <td><code>a or b</code></td>
          </tr>
        </tbody>
        <thead>
          <tr>
            <th>Spezialoperatoren</th>
            <th/>
            <th/>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Exklusives Oder</td>
            <td className="has-background-grey-light">a &oplus; b</td>
            <td><code>a xor b</code></td>
          </tr>
          <tr>
            <td>Negiertes Und</td>
            <td className="has-background-grey-light">a &#x22bc; b</td>
            <td><code>a nand b</code></td>
          </tr>
          <tr>
            <td>Negiertes Oder</td>
            <td className="has-background-grey-light">a &#x22bd; b</td>
            <td><code>a nor b</code></td>
          </tr>
          <tr>
            <td>Äquivalenz</td>
            <td className="has-background-grey-light">a &hArr; b</td>
            <td><code>a equiv b</code></td>
          </tr>
          <tr>
            <td>Implikation</td>
            <td className="has-background-grey-light">a &rArr; b</td>
            <td><code>a impl b</code></td>
          </tr>
        </tbody>
      </table>

      <h3 className="subtitle is-4 has-text-info has-text-centered">Weitere Regeln</h3>
      <div className="content">
        <ul>
          <li>Groß- und Kleinschreibung spielt keine Rolle.</li>
          <li>
            Reihenfolge der Auswertung:
            <ul>
              <li>Die Operatorrangfolge ist folgendermaßen festgelegt:
                <ol>
                  <li>Nicht</li>
                  <li>Und</li>
                  <li>Oder</li>
                  <li>Implikation</li>
                  <li>Restliche Operatoren: exklusives Oder, Nicht Und, Nicht Oder, äquivalenz</li>
                </ol>
              </li>
              <li>Beeinflussung der Auswertung:
                <ul>
                  <li>Klammere, um die Reihenfolge festzulegen, in der Ausdrücke ausgewertet werden. Beispiel:
                    <code>not((a or b) and c)</code></li>
                  <li>Ansonsten wird von links nach rechts ausgewertet (bei gleicher Präzedenz der zweite Operator Vorrang)
                  </li>
                </ul>
              </li>
              <li>Beispiele:
                <ul>
                  <li>Nach Rangfolge:
                    <code>a xor b impl c or d and not e</code> &hArr; <code>a xor (b impl (c or (d and (not e))))))</code>
                  </li>
                  <li>Gleichwertiger Rang (links nach rechts):
                    <code>a xor b nor c nand d equiv e</code> &hArr; <code>((((a xor b) nor c) nand d) equiv e)</code></li>
                </ul>
              </li>
            </ul>
          </li>
        </ul>

        <br/>
      </div>

    </>
  );
}
