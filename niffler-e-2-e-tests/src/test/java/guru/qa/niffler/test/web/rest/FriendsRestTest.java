package guru.qa.niffler.test.web.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.RestTestTest;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.UserDBClient;
import guru.qa.niffler.utils.RandomData;
import jaxb.userdata.FriendshipStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RestTestTest
public class FriendsRestTest {

    @RegisterExtension
    private static ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLoginExtension();
    private  final GatewayApiClient gatewayClient = new GatewayApiClient();
    public static final UserDBClient userDBClient = new UserDBClient();

    @User(withFriend = 1, withInInvite = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final List<UserJson> responseBody = gatewayClient.allFriends("Bearer " + bearerToken, null);
        assertEquals(3, responseBody.size());
    }
    @User(withFriend = 1, withInInvite = 1)
    @ApiLogin
    @Test
    void checkListWithFriendsAndIncomeInvitationsReturnsWithFilteringByUsername(UserJson user,@Token String userToken) {
        UserJson expectedFriend = user.testData().friends().getFirst();
        UserJson expectedIncomeInvitation = user.testData().incomeInvitations().getFirst();
        List<UserJson> responseBody = gatewayClient.allFriends("Bearer " + userToken, null);
        assertEquals(2, responseBody.size());
        UserJson actualIncomeInvitation = responseBody.get(0);
        UserJson actualFriend = responseBody.get(1);
        assertEquals(FriendshipStatus.INVITE_RECEIVED, actualIncomeInvitation.friendshipStatus());
        assertEquals(expectedIncomeInvitation.username(), actualIncomeInvitation.username());
        assertEquals(FriendshipStatus.FRIEND, actualFriend.friendshipStatus());
        assertEquals(expectedFriend.username(), actualFriend.username());
    }
    @User(withFriend = 1)
    @ApiLogin
    @Test
    void checkRemovedFriends(UserJson user, @Token String userToken) {
        String  friendForRemove = user.testData().friends().getFirst().username();
        gatewayClient.removeFriend("Bearer " + userToken, friendForRemove);
        assertTrue(gatewayClient.allFriends("Bearer " + userToken, null).isEmpty());
    }
    @User(withInInvite = 1)
    @ApiLogin
    @Test
    void checkAcceptingFriendshipRequest(UserJson user, @Token String userToken){
        UserJson friendFromRequest = user.testData().incomeInvitations().getFirst();
        UserJson futureFriend = new UserJson(friendFromRequest.username());
        gatewayClient.acceptInvitation("Bearer " + userToken, futureFriend);
        List<UserJson> listOfFriends = gatewayClient.allFriends("Bearer " + userToken, null);
        assertEquals(friendFromRequest.username(), listOfFriends.getFirst().username());
    }
    @User(withInInvite = 1)
    @ApiLogin
    @Test
    void checkRejectAFriendRequest(UserJson user, @Token String userToken){
        UserJson friendRequestSender = user.testData().incomeInvitations().getFirst();
        UserJson friendRequestSenderJson = new UserJson(friendRequestSender.username());
        UserJson rejectedUser = gatewayClient.declineInvitation("Bearer " + userToken, friendRequestSenderJson);
        List<UserJson> listOfFriends = gatewayClient.allFriends("Bearer " + userToken, null);
        assertEquals(0, listOfFriends.size());
        assertNull(rejectedUser.friendshipStatus());
    }
    @User
    @ApiLogin
    @Test
    void checkSendingFriendRequest(UserJson user, @Token String userToken){
        String friendUsername = RandomData.randomUserName();
        UserJson friendUser = userDBClient.createUser(
                friendUsername,
                "12345"
        );
        gatewayClient.sendInvitation(
                "Bearer " + userToken,
                friendUser
        );
        String actualOutcomeInvitation = gatewayClient.allUsers(
                "Bearer " + userToken,
                friendUser.username()
        ).getFirst().username();
        assertEquals(friendUsername, actualOutcomeInvitation);
    }
}
