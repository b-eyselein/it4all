def xmas_tree_top_simple(row: int, tree_height: int) -> str:
    offset: int = (tree_height - 1 - row)
    width: int = 2 * row + 1
    return '# ' + (' ' * offset) + ('*' * width) + (' ' * offset) + ' #'


def xmas_tree_top_design(row: int, tree_height: int) -> str:
    offset: int = (tree_height - 1 - row)
    deco: str = 'J' if row % 2 == 0 else 'o'

    return '# ' + (' ' * offset) + '*' * row + '*' + (' ' * offset) + ' #'


def xmas_tree_stub(h: int) -> str:
    offset: int = h - 1
    return '# ' + (' ' * offset) + 'I' + (' ' * offset) + ' #'


def xmas_tree_simple(treetop_height: int, stub_height: int) -> str:
    complete_width = 2 * treetop_height + 3

    tree: str = '#' * complete_width

    for i in range(treetop_height):
        tree += '\n' + xmas_tree_top_simple(i, treetop_height)

    for i in range(stub_height):
        tree += '\n' + xmas_tree_stub(treetop_height)

    return tree + '\n' + '#' * complete_width


def xmas_tree_design(treetop_height: int, stub_height: int) -> str:
    complete_width = 2 * treetop_height + 3

    tree: str = '#' * complete_width

    for i in range(treetop_height):
        tree += '\n' + xmas_tree_top_design(i, treetop_height)

    for i in range(stub_height):
        tree += '\n' + xmas_tree_stub(treetop_height)

    return tree + '\n' + '#' * complete_width
