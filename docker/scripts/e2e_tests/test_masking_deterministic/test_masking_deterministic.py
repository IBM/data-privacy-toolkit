"""
Copyright IBM Corp. 2020
"""

def test_validate(docker_run, compare_json_content):
    """
    The test.
    """
    docker_run(__file__)

    compare_json_content(__file__)
