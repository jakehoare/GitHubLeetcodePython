_author_ = 'jake'
_project_ = 'leetcode'

# https://leetcode.com/problems/longest-increasing-subsequence/
# Given an unsorted array of integers, find the length of longest increasing subsequence.

# Maintain a list of the smallest final memeber of longest increasing subsequence of each length.  Binary search this
# list to find the sequence that each num can extend.  This either creates a new longest sequence, improves some other
# existing length sequence, or does nothing.
# Time - O(nlogn)
# Space - O(n)

class Solution(object):
    def lengthOfLIS(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        LIS = []        # LIS[i] is the smallest number at the end of an increasing subsequence of length i+1

        for num in nums:
            list_nb = self.binary_search(num, LIS)
            if list_nb == len(LIS)-1:   # num extends longest list
                LIS.append(num)
            else:                       # num extends LIS[list_nb]
                LIS[list_nb+1] = min(num, LIS[list_nb+1])

        return len(LIS)


    def binary_search(self, num, LIS):  # return the index in LIS of the smallest number < num
        left, right = 0, len(LIS)-1     # or -1 if no such number

        while left <= right:

            mid = (left + right) // 2
            if num <= LIS[mid]:         # if num equals or is less than LIS[mid] then it cannot extend mid sequence
                right = mid-1           # so look at all shorter sequences
            else:
                left = mid+1

        return right