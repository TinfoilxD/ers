angular.module('ers', [ 'ui.router', 'ui.bootstrap', 'ui.bootstrap.modal' ]);
// angular.module('ers').config(function($stateProvider) {
angular.module('ers').config(
		function($stateProvider, $urlRouterProvider, $locationProvider) {
			$locationProvider.hashPrefix(""); // don't use !# in URL
			// if not recognized url then do this. might cause problems in the
			// future with ajax requests
			$urlRouterProvider.otherwise("/")
			$stateProvider.state({
				name : 'login',
				url : '/',
				templateUrl : 'login.html',
				controller : 'loginCtrl'
			}).state({
				name : 'employee',
				url : '/employee',
				templateUrl : 'employee.html',
				controller : 'employeeCtrl'
			}).state({
				name : 'ticket',
				templateUrl : 'create_ticket.html',
				controller : 'employeeCreateCtrl'
			}).state({
				name : 'financeManager',
				url : '/financeManager',
				templateUrl : 'finance_manager.html',
				controller : 'financeManagerCtrl'
			})
		});
angular.module('ers').controller('loginCtrl',
		function($scope, $http, $state, $rootScope) {
			$scope.logInFail = false; // used to display notification/warning
										// for
			// logins
			$scope.login = function() {
				console.log($scope.credentials)
				$http({
					method : 'POST',
					url : 'authentication/login',
					data : $scope.credentials
				}).then(function(data, status, headers, config) {

					console.log(data)
					var role = data.data.userRole.userRole
					$rootScope.user = data.data;
					if (role === 'Employee')
						$state.go('employee');
					if (role === 'Finance Manager')
						$state.go('financeManager');

				}, function(reason) {
					$scope.loginFail = true;
				}, function(value) {

				});
			}
		});
angular.module('ers').controller('employeeCtrl',
		function($scope, $rootScope, $http, $state, $document, $modal) {
	
			
			
			$document.ready(function() {
				$scope.getReimbursements();
			});
			$scope.$on("empRefresh", function() {
				$scope.getReimbursements();
			});
			$scope.getReimbursements = function() {
				$http({
					method : 'GET',
					url : 'content/getPastTickets'
				}).then(function(data, status, headers, config) {
					$scope.reims = data.data
					console.log($scope.reims);
				}, function(reason) {

				}, function(value) {

				})
			}
			$scope.openCreateTicket = function() {
				var modalInstance = $modal.open({
					templateUrl : 'create_ticket.html',
					controller : 'employeeCreateCtrl',
					size : 'sm',
				});
			}
			$scope.logout = function(){
				$state.go('login');
			}
		});
angular.module('ers').controller('employeeCreateCtrl',
		function($scope, $http, $state, $document, $modalInstance,$rootScope) {

			$document.ready(function() {
				$scope.fillTypeDropDown();
			})

			$scope.fillTypeDropDown = function() {
				$http({
					method : 'GET',
					url : 'content/getReimbursementTypes'
				}).then(function(data, status, headers, config) {
					console.log(data);
					$scope.reimTypes = data.data

				}, function(reason) {

				}, function(value) {

				})

			}

			$scope.createTicket = function() {
				$http({
					method : 'POST',
					url : 'content/createReimbursement',
					data : $scope.reim
				}).then(function(data, status, headers, config) {
					console.log(data.data)
					$rootScope.$broadcast('empRefresh');
					$modalInstance.close();
				}, function(reason) {

				}, function(value) {

				});
			}
		});
angular.module('ers').controller(
		'financeManagerCtrl',
		function($modal, $scope, $http, $state, $document, $filter) {
			$document.ready(function() {
				$scope.getStatuses();
				$scope.getAllReimbursements();
			});
			$scope.$on("finRefresh", function() {
				$scope.getAllReimbursements();
			});
			$scope.openConfirmAction = function(approved, reim, statusList) {
				var modalInstance = $modal.open({
					templateUrl : 'finance_manager_process.html',
					controller : 'financeManagerProcessCtrl',
					size : 'sm',
					resolve : {
						approved : function() {
							return approved;
						},
						reim : function() {
							return reim;
						},
						statusList : function() {
							return statusList;
						}
					}
				});
			}
			$scope.getStatuses = function() {
				$http({
					method : 'GET',
					url : 'content/getReimbursementStatus'
				}).then(function(data, status, headers, config) {
					$scope.statusList = data.data
					$scope.statusList.unshift($scope.filter)
					$scope.status = $scope.statusList[0]
				}, function(reason) {

				}, function(value) {

				})
			}
			$scope.getAllReimbursements = function() {
				$http({
					method : 'POST',
					url : 'content/getAllReimbursements',
					data : $scope.status
				}).then(function(data, status, headers, config) {
					$scope.reims = data.data
					$scope.displayedReims = $scope.reims;
				}, function(reason) {

				}, function(value) {

				})
			}

			$scope.logout = function(){
				$state.go('login');
			}
			/*
			 * Filter the table to save time on database calls Very makeshift
			 * solution to filtering.
			 * 
			 */
			$scope.filter = { //placeholder filter shifted in front of other statuses
					id: 0,
					status: 'Select All'
			}
			$scope.filterTable = function(reim) {
				var filter = $scope.status.status;
				if (filter !== "Pending" && filter !== "Approved"
						&& filter !== "Denied")
					return true;
				if (reim.status.status === filter) {
					return true;
				}
			}
		});

// this is the controller for the processRequest modal view
angular.module('ers').controller(
		'financeManagerProcessCtrl',
		function(approved, reim, statusList, $scope, $rootScope, $http, $state,
				$modalInstance) {

			$scope.approved = approved;
			$scope.reim = reim;
			$scope.statusList = statusList;
			console.log(approved);
			console.log(reim);

			$scope.cancelModal = function() {
				$modalInstance.close();
			}

			$scope.processRequest = function(approved) {
				if (approved) {
					$scope.approvedStatus = findStatus("Approved",
							$scope.statusList);
				} else {
					$scope.approvedStatus = findStatus("Denied",
							$scope.statusList);
				}
				$scope.reim.status = $scope.approvedStatus;
				$scope.reim.resolved = null; // jackson doesn't like this
				// java date time even with new
				// jsr310
				$scope.reim.submitted = null;
				console.log($scope.reim); // logs state of processedReim after
				// the status has been changed.
				// should equal approved

				$http({
					method : 'POST',
					url : 'content/processReimbursement',
					data : angular.toJson($scope.reim)
				}).then(function(value) {
					console.log(value)
					$rootScope.$broadcast('finRefresh');
				}, function(reason) {

				}, function(value) {

				})

				$modalInstance.close();
			}

			var findStatus = function(status, statusList) { // searches for a
				// status with a
				// matching string
				// since the database might change the ids if its recreated
				// somewhere else.
				for (key in statusList) {
					if (statusList[key].status === status) {
						return statusList[key];
					}
				}
			}
		})
