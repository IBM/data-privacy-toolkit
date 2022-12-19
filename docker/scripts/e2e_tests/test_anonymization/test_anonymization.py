"""
 Copyright IBM Corp. 2020
"""

def test_validate(docker_run, compare_entire_content, ):
    """
    The test.
    """
    docker_run(__file__)

    compare_entire_content(__file__)
