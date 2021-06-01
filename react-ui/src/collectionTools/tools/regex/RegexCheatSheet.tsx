import React from "react";

export function RegexCheatSheet(): JSX.Element {
  return (
    <table className="table is-bordered is-fullwidth">
      <thead>
        <tr>
          <th>Symbol</th>
          <th>Bedeutung</th>
          <th>Symbol</th>
          <th>Bedeutung</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><code>\d</code></td>
          <td>Eine Ziffer zwischen 0 und 9</td>
          <td><code>\D</code></td>
          <td>Zeichen, das keine Ziffer ist</td>
        </tr>
        <tr>
          <td><code>\s</code></td>
          <td>Leerzeichen, Tabulator, ...</td>
          <td><code>\w</code></td>
          <td>Buchstabe, Ziffer oder Unterstrich</td>
        </tr>
        <tr>
          <td><code>.</code></td>
          <td>Ein beliebiges Zeichen</td>
          <td><code>\.</code></td>
          <td>Ein Punkt</td>
        </tr>
        <tr>
          <td><code>+</code></td>
          <td>Ein oder mehr Vorkommen</td>
          <td><code>&#123;x&#125;</code></td>
          <td>Genau x Vorkommen</td>
        </tr>
        <tr>
          <td><code>&#123;x,y&#125;</code></td>
          <td>x bis y Vorkommen</td>
          <td><code>&#123;x,&#125;</code></td>
          <td>x bis unendlich viele Vorkommen</td>
        </tr>
        <tr>
          <td><code>*</code></td>
          <td>Beliebig viele (auch 0) Vorkommen</td>
          <td><code>?</code></td>
          <td>Ein oder kein Vorkommen</td>
        </tr>
        <tr>
          <td><code>x | y</code></td>
          <td>x oder y</td>
          <td><code>[x-y]</code></td>
          <td>Ein Zeichen im Bereich zwischen x und y</td>
        </tr>
      </tbody>
    </table>
  );
}
