"""
Copyright IBM Corp. 2020
"""


def test_validate(docker_run, compare_csv_total_rows_and_cols):
    """
    The test.
    """
    docker_run(__file__)

    compare_csv_total_rows_and_cols(__file__)
